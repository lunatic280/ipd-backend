package com.example.Ipd.gcoo.service

import com.example.Ipd.error.exception.userError.UserNotEqualsException
import com.example.Ipd.gcoo.domain.Gcoo
import com.example.Ipd.gcoo.dto.GcooDto
import com.example.Ipd.gcoo.repository.GcooRepository
import com.example.Ipd.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
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
class GcooService(
    private val gcooRepository: GcooRepository,
    private val userRepository: UserRepository,
    @Value("\${app.upload.gcoo_dir}") private val gcooUploadDir: String,
    @Value("\${app.base-url}") private val baseUrl: String
) {

    private fun saveImage(image: MultipartFile): String {
        val uploadPath = Paths.get(gcooUploadDir)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }

        val extension = image.originalFilename
            ?.substringAfterLast('.', "jpg")
            ?.ifBlank { "jpg" }
            ?: "jpg"
        val fileName = "${UUID.randomUUID()}.$extension"
        val filePath = uploadPath.resolve(fileName)
        Files.copy(image.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        return "$baseUrl/uploads/gcoo/$fileName"
    }

    fun getGcooById(gcooId: Long): GcooDto {
        val findGcoo = gcooRepository.findById(gcooId)
            .orElseThrow { NoSuchElementException("Not Found Gcoo: $gcooId") }
        return findGcoo.toDto()
    }

    fun getAllGcoos(): List<GcooDto> {
        return gcooRepository.findAll().map { it.toDto() }
    }

    @Transactional
    fun createGcoo(gcooDto: GcooDto, image: MultipartFile): GcooDto {
        if (image.isEmpty) {
            throw IllegalArgumentException("image file is required.")
        }
        require(gcooDto.subTitle.isNotBlank()) { "subTitle is required." }
        require(gcooDto.description.isNotBlank()) { "description is required." }
        val user = userRepository.findById(gcooDto.userId)
            .orElseThrow { NoSuchElementException("Not Found User: ${gcooDto.userId}") }
        val imageUrl = saveImage(image)
        val gcoo = Gcoo(
            id = null,
            charge = gcooDto.charge,
            distance = gcooDto.distance,
            subTitle = gcooDto.subTitle,
            description = gcooDto.description,
            imageUrl = imageUrl,
            latitude = gcooDto.latitude,
            longitude = gcooDto.longitude,
            user = user
        )
        return gcooRepository.save(gcoo).toDto()
    }

    @Transactional
    fun updateGcoo(gcooId: Long, newGcoo: GcooDto, userId: Long, image: MultipartFile?): GcooDto {
        val findUser = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("Not Found User: $userId") }
        val findGcoo = gcooRepository.findById(gcooId)
            .orElseThrow { NoSuchElementException("Not Found Gcoo $gcooId") }
        if (findGcoo.user.id != findUser.id) {
            throw UserNotEqualsException()
        }

        findGcoo.setCharge(newGcoo.charge)
        findGcoo.setDistance(newGcoo.distance)
        findGcoo.setSubTitle(newGcoo.subTitle)
        findGcoo.setDescription(newGcoo.description)
        findGcoo.setLatitude(newGcoo.latitude)
        findGcoo.setLongitude(newGcoo.longitude)
        if (image != null && !image.isEmpty) {
            val imageUrl = saveImage(image)
            findGcoo.setImageUrl(imageUrl)
        } else {
            newGcoo.imageUrl
                ?.takeIf { it.isNotBlank() }
                ?.let { findGcoo.setImageUrl(it) }
        }

        return findGcoo.toDto()

    }

    @Transactional
    fun deleteGcoo(gcooId: Long, userId: Long) {
        val findGcoo = gcooRepository.findById(gcooId)
            .orElseThrow { NoSuchElementException("Not Found Gcoo: $gcooId") }
        if (findGcoo.user.id != userId) {
            throw UserNotEqualsException()
        }
        return gcooRepository.deleteById(gcooId)
    }
}
