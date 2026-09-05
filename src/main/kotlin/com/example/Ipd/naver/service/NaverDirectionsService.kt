package com.example.Ipd.naver.service

import com.example.Ipd.naver.dto.DirectionsForAppResponse
import com.example.Ipd.naver.dto.NaverDirectionsResponse
import com.example.Ipd.naver.dto.toAppDto
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class NaverDirectionsService(
    private val naverDirectionsWebClient: WebClient
) {

    fun getDirections(
        startLat: Double,
        startLng: Double,
        goalLat :Double,
        goalLng: Double
    ): Mono<DirectionsForAppResponse> {
        val start = "$startLng,$startLat"
        val goal = "$goalLng,$goalLat"
        return naverDirectionsWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/driving")
                    .queryParam("start", start)
                    .queryParam("goal", goal)
                    .queryParam("option", "trafast")
                    .queryParam("cartype", 6)
                    .build()
            }
            .retrieve()
            .onStatus({ it.isError }) { resp ->
                resp.releaseBody().thenReturn(RuntimeException("외부 지도 API 요청에 실패했습니다."))
            }
            .bodyToMono(NaverDirectionsResponse::class.java)
            .map { it.toAppDto() }
    }

}
