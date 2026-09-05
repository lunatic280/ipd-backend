package com.example.Ipd.community.controller

import com.example.Ipd.community.dto.BlogLikeDto
import com.example.Ipd.community.service.BlogLikeService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.log

@RestController
@RequestMapping("/blogLike")
class BlogLikeController(
    private val blogLikeService: BlogLikeService
) {

    @PostMapping("/{blogId}/likes/toggle")
    fun toggleBlogLike(
        @PathVariable blogId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<BlogLikeDto> {
        return runCatching {
            val toggleLike = blogLikeService.toggleLike(user.getId(), blogId)
            ResponseEntity.ok().body(toggleLike)
        } .getOrElse {
            exception ->
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{blogId}/like/status")
    fun getBlogLikeStatus(
        @PathVariable("blogId") blogId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<BlogLikeDto> {
        return runCatching {
            val getStatus = blogLikeService.getBlogLikeStatus(blogId, user.getId())
            ResponseEntity.ok().body(getStatus)
        }.getOrElse { exception ->
            ResponseEntity.badRequest().build()
        }
    }
}
