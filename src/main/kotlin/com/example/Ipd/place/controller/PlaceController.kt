package com.example.Ipd.place.controller

import com.example.Ipd.place.dto.PlaceDto
import com.example.Ipd.place.service.CommentService
import com.example.Ipd.place.service.PlaceService
import com.example.Ipd.user.service.CustomUserDetails
import com.fasterxml.jackson.databind.ObjectMapper

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
@RequestMapping("/place")
class PlaceController(
    private val placeService: PlaceService,
    private val commentService: CommentService,
    private val objectMapper: ObjectMapper
) {


    @PostMapping("/create", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPlace(
        @RequestPart("request") request: PlaceDto,
        @RequestPart("images", required = false) images: List<MultipartFile>?,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<PlaceDto> {
        val savedPlace = placeService.createPlace(request, loginUser.getId(), images)
        val uri = URI.create("/${savedPlace.id}")
        return ResponseEntity.created(uri).body(savedPlace)
    }

    @GetMapping("/{id}")
    fun getPlace(@PathVariable("id") placeId: Long): ResponseEntity<PlaceDto> {
        val getPlaceId = placeService.getPlaceId(placeId)
        return ResponseEntity.ok(getPlaceId)
    }

    @GetMapping("/")
    fun getPlaceList(): ResponseEntity<List<PlaceDto>> {
        val list = placeService.getAllPlace()
        return ResponseEntity.ok(list)
    }

    @PatchMapping("/update/{id}")
    fun updatePlace(
        @PathVariable("id") placeId: Long,
        @RequestBody newPlace: PlaceDto,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<PlaceDto> {
        val updatePlace = placeService.updatePlaceDescription(
            placeId,
            newPlace.description,
            user.getId())
        return ResponseEntity.ok(updatePlace)
    }

    @DeleteMapping("/delete/{id}")
    fun deletePlace(
        @PathVariable("id") placeId: Long,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<Any> {
        placeService.deletePlace(placeId, loginUser.getId())
        return ResponseEntity.noContent().build()
    }



}
