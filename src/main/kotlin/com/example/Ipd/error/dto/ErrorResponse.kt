package com.example.Ipd.error.dto

import java.time.OffsetDateTime

data class ErrorResponse(
    val code: String,
    val message: String,
    val status: Int,
    val path: String?,
    val errors: List<FieldErrorDetail>? = null,
    val timestamp: String = OffsetDateTime.now().toString()
)
