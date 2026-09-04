package com.example.Ipd.review.dto

import com.example.Ipd.review.domain.TagType

data class TagDto(
    val id: Long,
    val name: String,
    val tagType: TagType
)
