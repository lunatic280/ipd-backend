package com.example.Ipd.review.service

import com.example.Ipd.error.exception.placeError.PlaceNotFoundException
import com.example.Ipd.error.exception.userError.UserNotEqualsException
import com.example.Ipd.place.repository.PlaceRepository
import com.example.Ipd.review.domain.Review
import com.example.Ipd.review.dto.ReviewDto
import com.example.Ipd.review.repository.ReviewRepository
import com.example.Ipd.review.repository.TagRepository
import com.example.Ipd.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.NoSuchElementException

// 전시용 원형: 업로드는 확장자 중심의 예제이며 안전한 이미지 검증을 보장하지 않습니다.
// 실제 운영에는 콘텐츠 디코딩, 크기 제한, 소유권 확인과 격리 저장이 필요합니다.
@Service
@Transactional(readOnly = true)
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val tagRepository: TagRepository,
    @Value("\${app.upload.review-dir}") private val reviewUploadDir: String,
    @Value("\${app.base-url}") private val baseUrl: String
) {

    private fun saveImages(images: List<MultipartFile>): List<String> {
        val uploadPath = Paths.get(reviewUploadDir)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }
        return images.filter { !it.isEmpty }.map { image ->
            val extension = image.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
            val fileName = "${UUID.randomUUID()}.$extension"
            val filePath = uploadPath.resolve(fileName)
            Files.copy(image.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
            "$baseUrl/uploads/review/$fileName"
        }
    }

    private fun collectImageUrls(dto: ReviewDto, images: List<MultipartFile>?): List<String> {
        val providedUrls = dto.imageUrl.filter { it.isNotBlank() }
        val uploadedUrls = images?.let { saveImages(it) } ?: emptyList()
        return providedUrls + uploadedUrls
    }

    fun getAllReview(): List<ReviewDto> {
        return reviewRepository.findAll().map { it.toDto() }
    }

    @Transactional
    fun createReview(dto: ReviewDto, images: List<MultipartFile>? = null): ReviewDto {
        val imageUrls = collectImageUrls(dto, images)

        val findUser = userRepository.findById(dto.userId)
            .orElseThrow { NoSuchElementException("User not found: ${dto.userId}") }
        val findPlace = placeRepository.findById(dto.placeId)
            .orElseThrow { PlaceNotFoundException(dto.placeId) }
        val bssComment = dto.bssComment
            ?: throw IllegalArgumentException("bssComment is required.")
        val reviewComment = dto.reviewComment
            ?: throw IllegalArgumentException("reviewComment is required.")
        val review = Review(
            id = null,
            user = findUser,
            place = findPlace,
            bssComment = bssComment,
            reviewComment = reviewComment,
            imageUrl = imageUrls.toMutableList()
        )
        val tagIds = dto.tagIds ?: emptyList()
        if (tagIds.isNotEmpty()) {
            val tags = tagRepository.findAllById(tagIds)

            val foundIds = tags.mapNotNull { it.id }.toSet()
            val requestIds = tagIds.toSet()
            val missingIds = requestIds - foundIds

            if (missingIds.isNotEmpty()) {
                throw IllegalArgumentException("Non-existent tag ids are included.")
            }
            review.setTag(tags)
        }
        return reviewRepository.save(review).toDto()
    }

    @Transactional
    fun updateReview(userId: Long, reviewId: Long, newDto: ReviewDto): ReviewDto {
        val findReview = reviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review not found: ${reviewId}") }
        if (findReview.user.id == null || findReview.user.id != userId) {
            throw UserNotEqualsException()
        }
        newDto.reviewComment?.let { findReview.updateReviewComment(it) }
        newDto.bssComment?.let { findReview.updateBssComment(it) }
        val updatedImageUrls = newDto.imageUrl.filter { it.isNotBlank() }
        if (updatedImageUrls.isNotEmpty()) {
            findReview.updateImageUrl(updatedImageUrls)
        }

        newDto.tagIds?.let { tagIds ->
            val tags = if (tagIds.isEmpty()) {
                emptyList()
            } else {
                tagRepository.findAllById(tagIds)
            }

            val foundIds = tags.mapNotNull { it.id }.toSet()
            val requestedIds = tagIds.toSet()
            val missingIds = requestedIds - foundIds

            if (missingIds.isNotEmpty()) {
                throw IllegalArgumentException("Non-existent tag ids are included.")
            }

            findReview.setTag(tags)
        }
        return findReview.toDto()
    }

    @Transactional
    fun deleteReview(userId: Long, reviewId: Long) {
        val findReview = reviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review not found: ${reviewId}") }
        if (userId == findReview.user.id) {
            reviewRepository.delete(findReview)
        } else {
            throw AccessDeniedException("Access denied user: ${userId}, author: ${findReview.user.id}")
        }
    }
}
