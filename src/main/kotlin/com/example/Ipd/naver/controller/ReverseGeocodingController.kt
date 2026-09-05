package com.example.Ipd.naver.controller

import com.example.Ipd.naver.dto.SimpleAddress
import com.example.Ipd.naver.dto.toSimpleAddress
import com.example.Ipd.naver.service.NaverReverseGeocodingService

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ReverseGeocodingController(
    private val reverseGeocodingService: NaverReverseGeocodingService
) {

    @GetMapping("/get")
    fun getLocation(
        @RequestParam lat: Double,
        @RequestParam lng: Double) : ResponseEntity<SimpleAddress?> {

        val geo = reverseGeocodingService.getReverseGeocodeing(lat, lng).toSimpleAddress()
        return runCatching {
            ResponseEntity.ok().body(geo)
        }.getOrElse { exception ->
            ResponseEntity.badRequest().build()
        }
    }
}
