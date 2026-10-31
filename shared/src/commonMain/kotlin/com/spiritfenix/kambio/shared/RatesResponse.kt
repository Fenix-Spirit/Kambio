package com.spiritfenix.kambio.shared

import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse (
	val base: String,
	val date: String,
	val lastUpdated: String,
	val rates: Map<String, Double>
)