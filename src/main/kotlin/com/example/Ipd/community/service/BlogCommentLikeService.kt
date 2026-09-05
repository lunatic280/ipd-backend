package com.example.Ipd.community.service

import com.example.Ipd.community.domain.BlogCommentLike
import com.example.Ipd.community.dto.BlogLikeDto
import com.example.Ipd.community.repository.BlogCommentLikeRepository
import com.example.Ipd.community.repository.BlogCommentRepository
import com.example.Ipd.community.repository.BlogLikeRepository
import com.example.Ipd.community.repository.BlogRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.Throws

@Service
@Transactional(readOnly = true)
class BlogCommentLikeService(
    private val blogCommentRepository: BlogCommentRepository,
    private val blogCommentLikeRepository: BlogCommentLikeRepository
) {

    @Transactional
    fun toggleBlogCommentLike(blogCommentId: Long, userId: Long): BlogLikeDto {
        val blogComment = blogCommentRepository.findById(blogCommentId)
            .orElseThrow { EntityNotFoundException("BlogComment not found: $blogCommentId") }

        val existing = blogCommentLikeRepository.findByCommentIdAndUserId( blogCommentId, userId)

        return if (existing != null) {
            blogCommentLikeRepository.delete(existing)
            val count = blogCommentLikeRepository.countByCommentId(blogCommentId)
            BlogLikeDto(
                liked = false,
                likeCount = count
            )
        } else {
            val like = BlogCommentLike(
                userId = userId,
                blogComment = blogComment
            )
            blogCommentLikeRepository.save(like)
            val count = blogCommentLikeRepository.countByCommentId(blogCommentId)
            BlogLikeDto(
                liked = true,
                likeCount = count
            )
        }
    }

    fun getBlogCommentLikeStatus(blogCommentId: Long, userId: Long): BlogLikeDto {
        if (!blogCommentRepository.existsById(blogCommentId)) {
            throw EntityNotFoundException("BlogComment not found: $blogCommentId")
        }

        val liked = blogCommentLikeRepository.existsByCommentIdAndUserId(blogCommentId, userId)
        val count = blogCommentLikeRepository.countByCommentId(blogCommentId)

        return BlogLikeDto(
            liked = liked,
            likeCount = count
        )
    }
}
