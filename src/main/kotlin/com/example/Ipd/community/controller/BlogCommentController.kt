package com.example.Ipd.community.controller

import com.example.Ipd.community.dto.BlogCommentDto
import com.example.Ipd.community.service.BlogCommentService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/blogComment")
class BlogCommentController(
    private val blogCommentService: BlogCommentService
) {

    @GetMapping("/")
    fun getAllBlogComment(): ResponseEntity<List<BlogCommentDto>> {
        val getBlogCommentList = blogCommentService.getAllBlogComment()
        return ResponseEntity.ok().body(getBlogCommentList)
    }

    @PostMapping("/create")
    fun createBlogComment(
        @RequestBody blogCommentDto: BlogCommentDto,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<BlogCommentDto> {
        val savedBlogComment = blogCommentService.createBlogComment(blogCommentDto, loginUser.getId())
        val uri = URI("/")
        return runCatching {
            ResponseEntity.created(uri).body(savedBlogComment)
        }.getOrElse { exception ->
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/{id}")
    fun addStar(@PathVariable("id") blogCommentId: Long): ResponseEntity<Int> {
        val findBlogComment = blogCommentService.addStarBlogComment(blogCommentId)
        return ResponseEntity.ok().body(findBlogComment)
    }

    @PatchMapping("/{id}")
    fun updateBlogComment(
        @PathVariable("id") blogCommentId: Long,
        @AuthenticationPrincipal loginUser: CustomUserDetails,
        @RequestBody blogCommentDto: BlogCommentDto
    ): ResponseEntity<BlogCommentDto> {
        val findBlogComment = blogCommentService.updateContent(
            blogCommentId,
            blogCommentDto,
            loginUser.getId()
        )
        return runCatching {
            ResponseEntity.ok().body(findBlogComment)
        }.getOrElse {
            exception ->
            ResponseEntity.badRequest().build()
        }

    }

    @DeleteMapping("/delete/{id}")
    fun deleteBlogComment(
        @PathVariable("id") blogCommentId: Long,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<out Any> {
        val delete = blogCommentService.deleteBlogComment(blogCommentId, loginUser.getId())
        return runCatching {
            ResponseEntity.ok().body(delete)
        }.getOrElse { exception ->
            ResponseEntity.badRequest().build()
        }
    }


}
