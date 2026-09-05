package com.example.Ipd.place.service

import com.example.Ipd.error.exception.placeError.CommentAccessDeniedException
import com.example.Ipd.error.exception.placeError.CommentNotFoundException
import com.example.Ipd.error.exception.placeError.PlaceNotFoundException
import com.example.Ipd.place.domain.PlaceComment
import com.example.Ipd.place.dto.CommentDto
import com.example.Ipd.place.repository.CommentRepository
import com.example.Ipd.place.repository.PlaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.AccessDeniedException

@Service
@Transactional(readOnly = true)
class CommentService(
    private val placeRepository: PlaceRepository,
    private val commentRepository: CommentRepository
) {

    fun getComment(commentId: Long): CommentDto {
        return commentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException(commentId) }.toDto()
    }

    fun getAllComment(placeId: Long): List<CommentDto> {
        val place = placeRepository.findById(placeId)
            .orElseThrow { PlaceNotFoundException(placeId) }
        val placeComments: List<PlaceComment> = commentRepository.findAllByPlace(place)
        return placeComments.map { it.toDto() }
    }

    @Transactional
    fun addComment(comment: CommentDto, currentUserId: Long): CommentDto {
        val place = placeRepository.findById(comment.placeId)
            .orElseThrow { PlaceNotFoundException(comment.placeId) }
        val savedPlaceComment = commentRepository.save(PlaceComment.create(place, comment.content, currentUserId))
        return savedPlaceComment.toDto()
    }

    @Transactional
    fun deleteComment(commentId: Long, currentUserId: Long) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException(commentId) }

        if (comment.createByUserId != currentUserId) {
            throw CommentAccessDeniedException()
        }

        commentRepository.delete(comment)
    }


}
