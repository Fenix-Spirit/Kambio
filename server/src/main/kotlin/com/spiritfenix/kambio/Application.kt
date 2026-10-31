package com.spiritfenix.kambio

import com.spiritfenix.kambio.shared.ErrorResponse
import com.spiritfenix.kambio.shared.RatesResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

fun main() {
	embeddedServer(
		Netty,
		port = 8080,
		host = "0.0.0.0",
		module = Application::module
	).start(wait = true)
}

fun Application.module() {
	val http = createHttpClient()
	val frankfurter = FrankfurterClient(http)
	monitor.subscribe(ApplicationStopped){http.close()}

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
	routing{
		get("/") { call.respondText("Currency Converter") }

		get("/api/rates/{base}") {
			val base = call.parameters["base"]!!.uppercase()
			val symbols = call.request.queryParameters["symbols"]?.split(",")?.map { it.trim().uppercase() }
			val upstream = frankfurter.latest(base, symbols)
			call.respond(
				RatesResponse(
					base = upstream.base,
					date = upstream.date,
					lastUpdated = Instant.now().toString(),
					rates = upstream.rates,
				)
			)
		}
	}
}
