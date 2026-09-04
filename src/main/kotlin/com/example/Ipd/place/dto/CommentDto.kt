package com.example.Ipd.place.dto

data class CommentDto(
    val id: Long? = null,
    val placeId: Long,
    val content: String
) {
    fun toDto(placeId: Long, content: String): CommentDto {
        return CommentDto(
            id = id,
            placeId = placeId,
            content = content
        )
    }


}
