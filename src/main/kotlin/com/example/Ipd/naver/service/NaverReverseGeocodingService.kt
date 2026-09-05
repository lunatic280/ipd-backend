package com.example.Ipd.naver.service

import com.example.Ipd.naver.dto.ReverseGeocodeResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class NaverReverseGeocodingService(
    @Qualifier("naverReverseGeocodeWebClient")
    private val webClient: WebClient
) {

    fun getReverseGeocodeing(lat: Double, lng: Double): ReverseGeocodeResponse {
        val coords = "$lng,$lat"

        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/gc")
                    .queryParam("coords", coords)
                    .queryParam("orders", "roadaddr,addr")
                    .queryParam("output", "json")
                    .build()
            }
            .retrieve()
            .onStatus({ it.isError }) { resp ->
                resp.releaseBody().thenReturn(RuntimeException("외부 지도 API 요청에 실패했습니다."))
            }
            .bodyToMono(ReverseGeocodeResponse::class.java)
            .block()
            ?: throw IllegalArgumentException("Reverse geocoding failed: empty body")
    }
}
