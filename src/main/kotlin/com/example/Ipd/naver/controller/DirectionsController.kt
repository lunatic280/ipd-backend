package com.example.Ipd.naver.controller

import com.example.Ipd.naver.dto.DirectionsForAppResponse
import com.example.Ipd.naver.service.NaverDirectionsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class DirectionsController(
    private val directionsService: NaverDirectionsService
) {

    @GetMapping("/directions")
    fun getDirections(
        @RequestParam startLat: Double,
        @RequestParam startLng: Double,
        @RequestParam goalLat: Double,
        @RequestParam goalLng: Double,
    ): Mono<DirectionsForAppResponse> {
        return directionsService.getDirections(startLat, startLng, goalLat, goalLng)
    }
}
