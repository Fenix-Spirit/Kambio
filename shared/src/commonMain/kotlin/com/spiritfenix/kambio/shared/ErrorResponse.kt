package com.spiritfenix.kambio.shared

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)