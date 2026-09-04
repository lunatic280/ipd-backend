package com.example.Ipd.gcoo.dto

data class GcooDto(
    val id: Long? = null,
    val charge: Int,
    val distance: Int,
    val subTitle: String,
    val description: String,
    val imageUrl: String? = null,
    val latitude: Double,
    val longitude: Double,
    val userId: Long
)
