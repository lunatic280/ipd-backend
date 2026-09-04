package com.example.Ipd.place.dto

import com.example.Ipd.place.domain.Place
import java.time.format.DateTimeFormatter

data class PlaceDto(
    val id: Long? = null,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String? = null,
    val likeCount: Int = 0,
    val telNumber: String,
    val type: String,
    val menus: List<Long> = emptyList(),
    val imageUrls: List<String> = emptyList()
)