package com.example.Ipd.community.dto

data class BlogCommentDto(
    val id: Long? = null,
    val blogId: Long,
    val content: String,
    val star: Int
)
