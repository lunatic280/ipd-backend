package com.example.Ipd.community.service

import com.example.Ipd.community.domain.BlogLike
import com.example.Ipd.community.dto.BlogLikeDto
import com.example.Ipd.community.repository.BlogLikeRepository
import com.example.Ipd.community.repository.BlogRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.PI

@Service
@Transactional(readOnly = true)
class BlogLikeService(
    private val blogLikeRepository: BlogLikeRepository,
    private val blogRepository: BlogRepository
) {

    @Transactional
    fun toggleLike(userId: Long, blogId: Long): BlogLikeDto {
        val blog = blogRepository.findById(blogId)
            .orElseThrow { EntityNotFoundException("Blog not found: $blogId") }

        val existing = blogLikeRepository.findByUserIdAndBlogId(userId, blogId)

        return if (existing != null) {
            blogLikeRepository.delete(existing)
            val count = blogLikeRepository.countByBlogId(blogId)
            BlogLikeDto(
                liked = false,
                likeCount = count
            )
        } else {
            val like = BlogLike(
                userId = userId,
                blog = blog
            )
            blogLikeRepository.save(like)

            val count = blogLikeRepository.countByBlogId(blogId)
            BlogLikeDto(
                liked = true,
                likeCount = count
            )
        }
    }

    fun getBlogLikeStatus(blogId: Long, userId: Long): BlogLikeDto {
        // 블로그 존재 여부는 blogRepository로 체크하는 것이 맞음
        if (!blogRepository.existsById(blogId)) {
            throw EntityNotFoundException("Blog not found: $blogId")
        }

        val liked = blogLikeRepository.existByUserIdAndBlogId(userId, blogId)
        val count = blogLikeRepository.countByBlogId(blogId)

        return BlogLikeDto(
            liked = liked,
            likeCount = count
        )
    }

}
