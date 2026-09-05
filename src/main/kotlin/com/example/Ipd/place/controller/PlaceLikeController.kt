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
@RequestMapping("/places")
class PlaceLikeController(
    private val placeLikeService: PlaceLikeService
) {

    @PostMapping("/{placeId}/likes/toggle")
    fun toggleLike(
        @PathVariable placeId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<PlaceLikeDto> {
        val toggleLike = placeLikeService.toggleLike(placeId, user.getId())
        return ResponseEntity.ok().body(toggleLike)
    }

    @GetMapping("/{placeId}/likes/status")
    fun getLikeStatus(
        @PathVariable placeId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<PlaceLikeDto> {
        val findLike = placeLikeService.getLikeStatus(placeId, user.getId())
        return ResponseEntity.ok().body(findLike)

    }
}
