package com.example.Ipd.menu.service

import com.example.Ipd.error.exception.placeError.PlaceNotFoundException
import com.example.Ipd.menu.domain.Menu
import com.example.Ipd.menu.dto.MenuDto
import com.example.Ipd.menu.repository.MenuRepository
import com.example.Ipd.place.repository.PlaceRepository
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
class MenuService(
    private val menuRepository: MenuRepository,
    @Value("\${app.upload.menu-dir}") private val menuUploadDir: String,
    @Value("\${app.base-url}") private val baseUrl: String,
    private val placeRepository: PlaceRepository
) {

    @Transactional
    fun createMenu(dto: MenuDto, image: MultipartFile): MenuDto {
        val findPlace = placeRepository.findById(dto.placeId)
            .orElseThrow { PlaceNotFoundException(dto.placeId) }

        if (image.isEmpty) {
            throw IllegalArgumentException("메뉴 이미지는 필수입니다.")
        }

        // 1) 원본 파일명 확인
        val originalFilename = image.originalFilename
            ?: throw IllegalArgumentException("이미지 파일 이름을 알 수 없습니다.")

        // 2) 확장자 추출 (콤마가 아닌 점(.) 기준)
        val ext = originalFilename
            .substringAfterLast('.', "")
            .lowercase()

        if (ext.isBlank()) {
            throw IllegalArgumentException("이미지 확장자를 찾을 수 없습니다. (파일명: $originalFilename)")
        }

        // 3) 허용 확장자 검증 (명확한 메시지 포함)
        require(ext in listOf("jpg", "jpeg", "png", "webp")) {
            "지원하지 않는 이미지 형식입니다. (확장자: .$ext)"
        }

        // 4) 파일명 생성
        val filename = "${UUID.randomUUID()}.$ext"

        // 5) 디렉터리 생성
        val dirPath = Paths.get(menuUploadDir)
        Files.createDirectories(dirPath)

        // 6) 파일 저장
        val filePath = dirPath.resolve(filename)
        image.inputStream.use { input ->
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING)
        }

        // 7) 외부에서 접근할 수 있는 URL
        val imageUrl = "$baseUrl/images/menu/$filename"

        // 8) 엔티티 생성
        val menu = Menu(
            name = dto.name,
            price = dto.price,
            description = dto.description,
            imageUrl = imageUrl,
            place = findPlace
        )

        return menuRepository.save(menu).toDto()
    }
    @Transactional
    fun updateMenu(menuId: Long, dto: MenuDto, image: MultipartFile?): MenuDto {
        val findMenu = menuRepository.findById(menuId)
            .orElseThrow { NoSuchElementException("Not Found Menu: $menuId") }

        findMenu.setName(dto.name)
        findMenu.setPrice(dto.price)
        findMenu.setDescription(dto.description)

        // 이미지가 있으면 새로 업로드
        if (image != null && !image.isEmpty) {
            val originalFilename = image.originalFilename
                ?: throw IllegalArgumentException("이미지 파일 이름을 알 수 없습니다.")

            val ext = originalFilename
                .substringAfterLast('.', "")
                .lowercase()

            if (ext.isBlank()) {
                throw IllegalArgumentException("이미지 확장자를 찾을 수 없습니다. (파일명: $originalFilename)")
            }

            require(ext in listOf("jpg", "jpeg", "png", "webp")) {
                "지원하지 않는 이미지 형식입니다. (확장자: .$ext)"
            }

            val filename = "${UUID.randomUUID()}.$ext"
            val dirPath = Paths.get(menuUploadDir)
            Files.createDirectories(dirPath)

            val filePath = dirPath.resolve(filename)
            image.inputStream.use { input ->
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING)
            }

            val imageUrl = "$baseUrl/images/menu/$filename"
            findMenu.setImageUrl(imageUrl)
        }

        return findMenu.toDto()
    }

    fun getByMenuId(menuId: Long): MenuDto {
        val findMenu = menuRepository.findById(menuId)
            .orElseThrow { NoSuchElementException("Not Found Menu: $menuId") }
        return findMenu.toDto()
    }

    fun getMenus(): List<MenuDto> =
        menuRepository.findAll().map { it.toDto() }

    @Transactional
    fun deleteMenu(menuId: Long) {
        return menuRepository.deleteById(menuId)
    }
}
