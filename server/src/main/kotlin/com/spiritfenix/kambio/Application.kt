package com.spiritfenix.kambio

import com.spiritfenix.kambio.db.RatesRepository
import com.spiritfenix.kambio.db.connectDatabase
import com.spiritfenix.kambio.shared.ErrorResponse
import com.spiritfenix.kambio.shared.RatesResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun main() {
	embeddedServer(
		Netty,
		port = 8080,
		host = "0.0.0.0",
		module = Application::module
	).start(wait = true)
}

fun Application.module() {
	val dataSource = connectDatabase()
	val http = createHttpClient()
	val service = RatesService(FrankfurterClient(http), RatesRepository())
	monitor.subscribe(ApplicationStopped){
		http.close()
		dataSource.close()
	}
	install(XForwardedHeaders)
	install(ContentNegotiation){json()}
	install(StatusPages){
		exception<UnknownCurrencyException> { call, cause ->
			call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message!!))
		}
		exception<UpstreamException> { call, cause ->
			call.application.log.error("Upstream rates provider failed", cause)
			call.respond(HttpStatusCode.BadGateway, ErrorResponse("Exchange rate provider unavailable"))
		}
	}
	install(RateLimit){
		register (RateLimitName("api")){
			rateLimiter(limit = 60, refillPeriod = 1.minutes)
		}
	}
	routing{
		get("/") { call.respondText("Currency Converter") }
		rateLimit(RateLimitName("api")){
			get("/api/rates/{base}") {
				val base = call.parameters["base"]!!.uppercase()
				val symbols = call.request.queryParameters["symbols"]?.split(",")?.map { it.trim().uppercase() }
				val cached = service.get(base)
				val rates = if (symbols.isNullOrEmpty()) {
					cached.rates
				} else {
					if (symbols.any { it !in cached.rates }) throw UnknownCurrencyException()
					cached.rates.filterKeys { it in symbols }
				}
				call.respond(
					RatesResponse(base, cached.date.toString(), cached.fetchedAt.toString(), rates)
				)
			}
			get("/api/currencies") {
				call.respond(service.get("EUR").rates.keys.sorted())
			}
		}
	}
	launch {
		while (isActive){
			service.refreshAll()
			delay(24.hours)
		}
	}
}
