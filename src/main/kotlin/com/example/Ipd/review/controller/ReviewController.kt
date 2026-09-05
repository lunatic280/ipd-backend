package com.example.Ipd.review.controller

import com.example.Ipd.review.dto.ReviewDto
import com.example.Ipd.review.service.ReviewService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
@RequestMapping("/review")
class ReviewController(
    private val reviewService: ReviewService
) {

    @GetMapping("/")
    fun getAllReview(): ResponseEntity<List<ReviewDto>> {
        val getListReview = reviewService.getAllReview()
        return ResponseEntity.ok().body(getListReview)
    }

    @PostMapping("/create", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createReview(
        @RequestPart("request") dto: ReviewDto,
        @RequestPart("images", required = false) images: List<MultipartFile>?,
        @RequestPart("image", required = false) image: MultipartFile?,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<ReviewDto> {
        val mergedImages = mutableListOf<MultipartFile>()
        images?.let { mergedImages.addAll(it.filterNot { file -> file.isEmpty }) }
        if (image != null && !image.isEmpty) {
            mergedImages.add(image)
        }

        val requestWithUser = dto.copy(userId = user.getId())
        val savedReview = reviewService.createReview(
            requestWithUser,
            if (mergedImages.isEmpty()) null else mergedImages
        )
        val uri = URI("/")
        return ResponseEntity.created(uri).body(savedReview)
    }

    @PutMapping("/update/{reviewId}")
    fun updateReview(
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal user : CustomUserDetails,
        @RequestBody newDto: ReviewDto
        ): ResponseEntity<ReviewDto> {
        val updatedReview = reviewService.updateReview(user.getId(), reviewId, newDto)
        return ResponseEntity.ok().body(updatedReview)
    }

    @DeleteMapping("/delete/{reviewId}")
    fun deleteReview(
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<Any> {
        reviewService.deleteReview(user.getId(), reviewId)
        return ResponseEntity.ok().build()
    }
}
