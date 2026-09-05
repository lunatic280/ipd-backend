package com.example.Ipd.place.service

import com.example.Ipd.error.exception.placeError.PlaceNotFoundException
import com.example.Ipd.place.domain.PlaceLike
import com.example.Ipd.place.dto.PlaceLikeDto
import com.example.Ipd.place.repository.PlaceLikeRepository
import com.example.Ipd.place.repository.PlaceRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PlaceLikeService(
    private val placeRepository: PlaceRepository,
    private val placeLikeRepository: PlaceLikeRepository
) {

    @Transactional
    fun toggleLike(placeId: Long, userId: Long): PlaceLikeDto {
        val place = placeRepository.findById(placeId)
            .orElseThrow { PlaceNotFoundException(placeId) }

        val existing = placeLikeRepository.findByUserIdAndPlaceId(userId, placeId)

        return if (existing != null) {
            placeLikeRepository.delete(existing)
            val count = placeLikeRepository.countByPlaceId(placeId)
            PlaceLikeDto(
                liked = false,
                likeCount = count
            )
        } else {
            val like = PlaceLike(
                userId = userId,
                place = place
            )
            placeLikeRepository.save(like)

            val count = placeLikeRepository.countByPlaceId(placeId)
            PlaceLikeDto(
                liked = true,
                likeCount = count
            )
        }
    }

    fun getLikeStatus(placeId: Long, userId: Long): PlaceLikeDto {
        if (!placeRepository.existsById(placeId)) {
            throw PlaceNotFoundException(placeId)
        }

        val liked = placeLikeRepository.existByUserIdAndPlaceId(userId, placeId)
        val count = placeLikeRepository.countByPlaceId(placeId)

        return PlaceLikeDto(
            liked = liked,
            likeCount = count
        )
    }

}
