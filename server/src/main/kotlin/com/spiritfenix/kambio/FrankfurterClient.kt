package com.spiritfenix.kambio

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://api.frankfurter.dev/v1"
class UnknownCurrencyException : Exception("Unknown base or currency symbol")
class UpstreamException(cause: Throwable) : Exception("Rates provider failed", cause)

@Serializable
internal data class FrankfurterResponse(
	val base: String,
	val date: String,
	val rates: Map<String, Double>,
)

fun createHttpClient() = HttpClient(CIO) {
	expectSuccess = true
	install(HttpTimeout) {
		connectTimeoutMillis = 5_000
		requestTimeoutMillis = 10_000
	}
	install(ContentNegotiation) {
		json(Json { ignoreUnknownKeys = true })
	}
}

internal class FrankfurterClient(private val http: HttpClient) {
	suspend fun latest(base: String, symbols: List<String>?): FrankfurterResponse {
		try {
			return http.get("$BASE_URL/latest") {
				parameter("base", base)
				if (!symbols.isNullOrEmpty()) parameter("symbols", symbols.joinToString(","))
			}.body()
		} catch (e: ClientRequestException) {
			when (e.response.status) {
				HttpStatusCode.NotFound, HttpStatusCode.UnprocessableEntity ->
					throw UnknownCurrencyException()
				else -> throw UpstreamException(e)
			}
		} catch (e: CancellationException) {
			throw e
		} catch (e: Exception) {
			throw UpstreamException(e)
		}
	}
}