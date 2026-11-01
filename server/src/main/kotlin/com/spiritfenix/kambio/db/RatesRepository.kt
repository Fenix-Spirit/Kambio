package com.spiritfenix.kambio.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.time.LocalDate

data class CachedRates(
	val date: LocalDate,
	val fetchedAt: Instant,
	val rates: Map<String, Double>,
)

class RatesRepository {
	suspend fun replace(base: String, cached: CachedRates) = withContext(Dispatchers.IO) {
		transaction {
			RatesTable.batchUpsert(cached.rates.entries) { (target, rate) ->
				this[RatesTable.base] = base
				this[RatesTable.target] = target
				this[RatesTable.rate] = rate
				this[RatesTable.rateDate] = cached.date
				this[RatesTable.fetchedAt] = cached.fetchedAt
			}
		}
	}
	suspend fun find(base: String): CachedRates? = withContext(Dispatchers.IO) {
		transaction {
			val rows = RatesTable.selectAll().where { RatesTable.base eq base }.toList()
			if (rows.isEmpty()) return@transaction null
			CachedRates(
				date = rows.first()[RatesTable.rateDate],
				fetchedAt = rows.maxOf { it[RatesTable.fetchedAt] },
				rates = rows.associate { it[RatesTable.target] to it[RatesTable.rate] },
			)
		}
	}
}