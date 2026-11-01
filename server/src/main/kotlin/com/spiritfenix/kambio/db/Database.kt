package com.spiritfenix.kambio.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object RatesTable : Table("rates") {
	val base = varchar("base", 3)
	val target = varchar("target", 3)
	val rate = double("rate")
	val rateDate = date("rate_date")
	val fetchedAt = timestamp("fetched_at")
	override val primaryKey = PrimaryKey(base, target)
}

fun connectDatabase(): HikariDataSource {
	val config = HikariConfig().apply {
		jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/kambio"
		username = System.getenv("DB_USER") ?: "kambio"
		password = System.getenv("DB_PASSWORD") ?: "kambio"
		maximumPoolSize = 5
	}
	val dataSource = HikariDataSource(config)
	Database.connect(dataSource)
	transaction { SchemaUtils.create(RatesTable) }
	return dataSource
}