package com.spiritfenix.kambio

import com.spiritfenix.kambio.db.CachedRates
import com.spiritfenix.kambio.db.RatesRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

internal class RatesService(
	private val client: FrankfurterClient,
	private val repo: RatesRepository,
) {
	private val logger: Logger = LoggerFactory.getLogger(RatesService::class.java)
	suspend fun refresh(base: String): CachedRates {
		val rows = client.latest(base)
		val date =rows.maxOfOrNull { it.date }?: throw UpstreamException(IllegalStateException("Empty rates response for $base"))
		val cached = CachedRates(
			LocalDate.parse(date),
			Instant.now(),
			rows.associate { it.quote to it.rate }
		)
		repo.replace(base, cached)
		return cached
	}
	suspend fun get(base: String): CachedRates = repo.find(base) ?: refresh(base)
	suspend fun refreshAll() {
		val bases = repo.bases().ifEmpty { listOf("EUR") }
		for (base in bases) {
			try {
				refresh(base)
			} catch (e: CancellationException) {
				throw e
			} catch (e: Exception) {
				logger.warn("Refresh failed for $base", e)
			}
		}
	}
}