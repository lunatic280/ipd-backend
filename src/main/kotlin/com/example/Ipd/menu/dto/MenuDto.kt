package com.example.Ipd.menu.dto

data class MenuDto(
    val id: Long?,
    val name: String,
    val price: Int,
    val description: String,
    val imageUrl: String,
    val placeId: Long
)
