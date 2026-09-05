package com.example.Ipd.community.service

import com.example.Ipd.community.domain.Blog
import com.example.Ipd.community.dto.BlogDto
import com.example.Ipd.community.repository.BlogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BlogService(
    private val blogRepository: BlogRepository
) {

    fun getBlogById(blogId: Long): BlogDto {
        val findBlogById = blogRepository.findById(blogId)
            .orElseThrow { NoSuchElementException("not found blog: $blogId") }
        return findBlogById.toDto()
    }

    fun getAllBlog(): List<BlogDto> {
        val blogList: List<BlogDto> = blogRepository.findAll().map { it.toDto() }
        return blogList
    }

    @Transactional
    fun createBlog(blogDto: BlogDto, loginUserId: Long): BlogDto {
        val savedBlog = blogRepository.save<Blog>(
            Blog(
                title = blogDto.title,
                content = blogDto.content,
                star = 0,
                createdByUserId = loginUserId
            )
        )
        return savedBlog.toDto()
    }

    @Transactional
    fun updateBlog(newBlogDto: BlogDto, blogId: Long, loginUserId: Long): BlogDto {
        val findBlogById = blogRepository.findById(blogId)
            .orElseThrow { NoSuchElementException("not found blog: $blogId") }
        if (loginUserId == findBlogById.createdByUserId) {
            findBlogById.updateTitle(newBlogDto.title)
            findBlogById.updateContent(newBlogDto.content)
        } else {
            IllegalArgumentException("같은 사용자가 아닙니다.")
        }
        return findBlogById.toDto()
    }

    @Transactional
    fun deleteBlog(blogId: Long, loginUserId: Long) {
        val findBlogById = blogRepository.findById(blogId)
            .orElseThrow{ NoSuchElementException("Not found: $blogId") }
        if (findBlogById.createdByUserId == loginUserId) {
            return blogRepository.delete(findBlogById)
        } else {
            IllegalArgumentException("사용자가 다릅니다.")
        }

    }
}
