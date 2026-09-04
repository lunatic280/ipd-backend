package com.example.Ipd.review.dto

data class ReviewDto(
    val id: Long? = null,
    val bssComment: String? = null,
    val reviewComment: String? = null,
    val placeId: Long,
    val userId: Long,
    val tagIds: List<Long>? = null,
    val imageUrl: List<String> = emptyList(),
)
