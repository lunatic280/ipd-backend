package com.example.Ipd.place.service

import com.example.Ipd.error.exception.placeError.CommentNotFoundException
import com.example.Ipd.place.domain.PlaceCommentLike
import com.example.Ipd.place.dto.PlaceLikeDto
import com.example.Ipd.place.repository.CommentRepository
import com.example.Ipd.place.repository.PlaceCommentLikeRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PlaceCommentLikeService(
    private val placeCommentLikeRepository: PlaceCommentLikeRepository,
    private val placeCommentRepository: CommentRepository
) {

    @Transactional
    fun toggleLike(commentId: Long, userId: Long): PlaceLikeDto {
        val comment = placeCommentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException(commentId) }

        val existing = placeCommentLikeRepository.findByUserIdAndCommentId(userId, commentId)

        return if (existing != null) {
            placeCommentLikeRepository.delete(existing)
            val count = placeCommentLikeRepository.countByCommentId(commentId)
            PlaceLikeDto(
                liked = false,
                likeCount = count
            )
        } else {
            val like = PlaceCommentLike(
                userId = userId,
                comment = comment
            )
            placeCommentLikeRepository.save(like)

            val count = placeCommentLikeRepository.countByCommentId(commentId)
            PlaceLikeDto(
                liked = true,
                likeCount = count
            )
        }
    }

    fun getLikeStatus(commentId: Long, userId: Long): PlaceLikeDto {
        if (!placeCommentRepository.existsById(commentId)) {
            throw CommentNotFoundException(commentId)
        }

        val liked = placeCommentLikeRepository.existByUserIdAndCommentId(userId, commentId)
        val count = placeCommentLikeRepository.countByCommentId(commentId)

        return PlaceLikeDto(
            liked = liked,
            likeCount = count
        )
    }

}
