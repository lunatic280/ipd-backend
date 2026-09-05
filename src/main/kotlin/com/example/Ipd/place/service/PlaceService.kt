package com.example.Ipd.place.service

import com.example.Ipd.error.exception.placeError.PlaceAccessDeniedException
import com.example.Ipd.error.exception.placeError.PlaceNotFoundException
import com.example.Ipd.error.exception.userError.UserNotEqualsException
import com.example.Ipd.place.domain.Place
import com.example.Ipd.place.dto.PlaceDto
import com.example.Ipd.place.repository.PlaceRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

// 전시용 원형: 업로드는 확장자 중심의 예제이며 안전한 이미지 검증을 보장하지 않습니다.
// 실제 운영에는 콘텐츠 디코딩, 크기 제한, 소유권 확인과 격리 저장이 필요합니다.
@Service
@Transactional(readOnly = true)
class PlaceService(
    private val placeRepository: PlaceRepository,
    @Value("\${app.upload.place-dir}") private val placeUploadDir: String,
    @Value("\${app.base-url}") private val baseUrl: String
) {

    private fun saveImages(images: List<MultipartFile>): List<String> {
        val uploadPath = Paths.get(placeUploadDir)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }
        return images.filter { !it.isEmpty }.map { image ->
            val extension = image.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
            val fileName = "${UUID.randomUUID()}.$extension"
            val filePath = uploadPath.resolve(fileName)
            Files.copy(image.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
            "$baseUrl/uploads/place/$fileName"
        }
    }

    fun getPlace(placeDto: PlaceDto): PlaceDto? {
        return placeRepository.findByTitle(placeDto.title)?.toDto()
    }

    fun getPlaceId(placeId: Long): PlaceDto {
        return placeRepository.findById(placeId)
            .orElseThrow { PlaceNotFoundException(placeId) }.toDto()
    }

    fun getAllPlace(): List<PlaceDto> {
        return placeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .map { it.toDto() }

    }


    @Transactional
    fun updatePlaceDescription(placeId: Long, newDescription: String, userId: Long): PlaceDto {
        val place: Place = placeRepository.findById(placeId)
            .orElseThrow { PlaceNotFoundException(placeId) }
        if (userId != place.createdByUserId) {
            throw UserNotEqualsException()
        }
        place.updateDescription(newDescription)
        return place.toDto()
    }

    @Transactional
    fun createPlace(placeDto: PlaceDto, currentUserId: Long, images: List<MultipartFile>?): PlaceDto {
        val imageUrls = images?.let { saveImages(it) } ?: emptyList()
        val savePlace = placeRepository.save(
            Place.create(
                placeDto.title,
                placeDto.description,
                placeDto.latitude,
                placeDto.longitude,
                telNumber = placeDto.telNumber,
                type = placeDto.type,
                createdByUserId = currentUserId,
                imageUrls = imageUrls
            )
        )
        return savePlace.toDto()
    }

    @Transactional
    fun deletePlace(placeId: Long, currentUserId: Long) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { PlaceNotFoundException(placeId) }

        if (place.createdByUserId != currentUserId) {
            throw PlaceAccessDeniedException()
        }

        placeRepository.delete(place)
    }

}
