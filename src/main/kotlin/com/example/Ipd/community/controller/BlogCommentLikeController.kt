package com.example.Ipd.community.controller

import com.example.Ipd.community.dto.BlogLikeDto
import com.example.Ipd.community.service.BlogCommentLikeService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/blogCommentLike")
class BlogCommentLikeController(
    private val blogCommentLikeService: BlogCommentLikeService
) {

    @PostMapping("/{blogCommentId}/like/toggle")
    fun toggleBlogCommentLike(
        @PathVariable("blogCommentId") blogCommentId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<BlogLikeDto> {
        return runCatching {
            val toggleLike = blogCommentLikeService.toggleBlogCommentLike(blogCommentId, user.getId())
            ResponseEntity.ok().body(toggleLike)

        }.getOrElse { exception ->
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{blogCommentId}/like/status")
    fun getBlogCommentLikeStatus(
        @PathVariable("blogCommentId") blogCommentId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<BlogLikeDto> {
        return runCatching {
            val getStatus = blogCommentLikeService.getBlogCommentLikeStatus(blogCommentId, user.getId())
            ResponseEntity.ok().body(getStatus)
        }.getOrElse { exception ->
            ResponseEntity.badRequest().build()
        }
    }

}
