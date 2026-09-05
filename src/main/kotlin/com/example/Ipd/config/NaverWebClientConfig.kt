package com.example.Ipd.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class NaverWebClientConfig(
    @Value("\${naver.map.directions.base-url}")
    private val baseUrl: String,

    @Value("\${naver.map.directions.reverse-base-url}")
    private val reverseBaseUrl: String,

    @Value("\${naver.map.directions.client-id}")
    private val clientId: String,

    @Value("\${naver.map.directions.client-secret}")
    private val clientSecret: String
) {

    @Bean
    fun naverReverseGeocodeWebClient(builder: WebClient.Builder): WebClient {
        return builder
            .baseUrl(reverseBaseUrl)
            .defaultHeader("X-NCP-APIGW-API-KEY-ID", clientId)
            .defaultHeader("X-NCP-APIGW-API-KEY", clientSecret)
            .build()
    }

    @Bean
    fun naverDirectionsWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("x-ncp-apigw-api-key-id", clientId)
            .defaultHeader("x-ncp-apigw-api-key", clientSecret)
            .build()
    }
}
