package com.example.Ipd.gcoo.controller

import com.example.Ipd.gcoo.dto.GcooDto
import com.example.Ipd.gcoo.service.GcooService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/gcoo")
class GcooController(
    private val gcooService: GcooService
) {


    /**
     * 전체 킥보드 목록 조회 (모든 사용자 가능)
     */
    @GetMapping("/")
    fun getAllGcoos(): ResponseEntity<List<GcooDto>> {
        val gcoos = gcooService.getAllGcoos()
        return ResponseEntity.ok(gcoos)
    }

    /**
     * 킥보드 생성 (OWNER만 가능)
     */
    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping("/create", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createGcoo(
        @RequestPart("request") dto: GcooDto,
        @RequestPart("image") image: MultipartFile,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<GcooDto> {
        val request = dto.copy(userId = loginUser.getId())
        val savedGcoo = gcooService.createGcoo(request, image)
        return ResponseEntity.ok().body(savedGcoo)
    }

    /**
     * 특정 킥보드 조회 (모든 사용자 가능)
     */
    @GetMapping("/{gcooId}")
    fun getGcoo(@PathVariable gcooId: Long): ResponseEntity<GcooDto> {
        val gcoo = gcooService.getGcooById(gcooId)
        return ResponseEntity.ok(gcoo)
    }

    /**
     * 킥보드 수정 (OWNER만 가능)
     */
    @PreAuthorize("hasAnyRole('OWNER')")
    @PatchMapping("/{gcooId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateGcoo(
        @PathVariable gcooId: Long,
        @RequestPart("request") dto: GcooDto,
        @RequestPart("image", required = false) image: MultipartFile?,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<GcooDto> {
        val request = dto.copy(userId = loginUser.getId())
        val updated = gcooService.updateGcoo(gcooId, request, loginUser.getId(), image)
        return ResponseEntity.ok(updated)
    }

    /**
     * 킥보드 삭제 (OWNER만 가능)
     */
    @PreAuthorize("hasAnyRole('OWNER')")
    @DeleteMapping("/{gcooId}")
    fun deleteGcoo(
        @PathVariable gcooId: Long,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<Void> {
        gcooService.deleteGcoo(gcooId, loginUser.getId())
        return ResponseEntity.noContent().build()
    }
}
