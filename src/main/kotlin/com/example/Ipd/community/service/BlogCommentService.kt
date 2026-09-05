package com.example.Ipd.community.service

import com.example.Ipd.community.domain.BlogComment
import com.example.Ipd.community.dto.BlogCommentDto
import com.example.Ipd.community.repository.BlogCommentRepository
import com.example.Ipd.community.repository.BlogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BlogCommentService(
    private val blogCommentRepository: BlogCommentRepository,
    private val blogRepository: BlogRepository
) {
    @Transactional
    fun addStarBlogComment(blogCommentId: Long): Int {
        val findBlogComment = blogCommentRepository.findById(blogCommentId)
            .orElseThrow { NoSuchElementException("Not found BlogComment: $blogCommentId") }
        findBlogComment.increaseStar()
        return findBlogComment.getStar()
    }

    fun getAllBlogComment(): List<BlogCommentDto> {
        val getAllList: List<BlogCommentDto> = blogCommentRepository.findAll().map { it.toDto() }
        return getAllList
    }

    @Transactional
    fun createBlogComment(blogCommentDto: BlogCommentDto, loginUserId: Long): BlogCommentDto {
        val findBlog = blogRepository.findById(blogCommentDto.blogId)
            .orElseThrow { NoSuchElementException("Not found Blog: ${blogCommentDto.blogId}") }
        val savedBlogComment = blogCommentRepository.save<BlogComment>(
            BlogComment.create(
                content = blogCommentDto.content,
                blog = findBlog,
                star = 0,
                createdByUserId = loginUserId,
            ))
        return savedBlogComment.toDto()
    }

    @Transactional
    fun updateContent(blogCommentId: Long, newBlogComment: BlogCommentDto, loginUserId: Long): BlogCommentDto {
        val findBlogComment = blogCommentRepository.findById(blogCommentId)
            .orElseThrow { NoSuchElementException("Not found BlogComment: ${newBlogComment.id}") }

        if (findBlogComment.createdByUserId == loginUserId) {
            findBlogComment.updateContent(newBlogComment.content)
        } else {
            throw IllegalStateException("사용자가 수정할 권한이 없습니다.")
        }
        return findBlogComment.toDto()
    }

    @Transactional
    fun deleteBlogComment(blogCommentId: Long, loginUserId: Long) {
        val findBlogComment = blogCommentRepository.findById(blogCommentId)
            .orElseThrow { NoSuchElementException("Not found BlogComment: $blogCommentId") }

        if (findBlogComment.createdByUserId != loginUserId) {
            throw IllegalAccessException("사용자가 삭제할 권한이 없습니다.")
        }
        blogCommentRepository.deleteById(blogCommentId)
    }

}
