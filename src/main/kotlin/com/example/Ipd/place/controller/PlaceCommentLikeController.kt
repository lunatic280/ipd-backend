package com.example.Ipd.place.controller

import com.example.Ipd.place.dto.PlaceLikeDto
import com.example.Ipd.place.service.PlaceLikeService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("comments")
class PlaceCommentLikeController(
    private val placeCommentLikeService: PlaceLikeService
) {

    @PostMapping("/{commentId}/likes/toggle")
    fun toggleLike(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<PlaceLikeDto>? {
        val postToggle = placeCommentLikeService.toggleLike(commentId, user.getId())
        return ResponseEntity.ok().body(postToggle)
    }

    @GetMapping("/{commentId}/likes/status")
    fun getLikeStatus(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<PlaceLikeDto> {
        val getLike = placeCommentLikeService.getLikeStatus(commentId, user.getId())
        return ResponseEntity.ok().body(getLike)
    }
}
