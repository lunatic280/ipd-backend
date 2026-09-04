package com.example.Ipd.error.dto

data class FieldErrorDetail(
    val field: String,
    val value: Any?,
    val reason: String
)
