package com.spiritfenix.kambio

import com.spiritfenix.kambio.db.CachedRates
import com.spiritfenix.kambio.db.RatesRepository
import java.time.Instant
import java.time.LocalDate

internal class RatesService(
	private val client: FrankfurterClient,
	private val repo: RatesRepository,
) {
	suspend fun refresh(base: String): CachedRates {
		val upstream = client.latest(base, symbols = null)
		val cached = CachedRates(LocalDate.parse(upstream.date), Instant.now(), upstream.rates)
		repo.replace(upstream.base, cached)
		return cached
	}

	suspend fun get(base: String): CachedRates = repo.find(base) ?: refresh(base)
}