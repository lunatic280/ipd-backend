package com.example.Ipd.naver.dto

data class DirectionsForAppResponse(
    val code: Int,
    val message: String,
    val distance: Int,           // 총 거리 (m)
    val duration: Long,          // 총 소요 시간 (ms)
    val path: List<LatLngDto>,   // 지도에 그릴 경로
    val guides: List<GuideDto>   // (선택) 안내 문구
)

data class LatLngDto(
    val lat: Double,
    val lng: Double
)

data class GuideDto(
    val distance: Int,
    val duration: Long,
    val instructions: String,
    val pointIndex: Int,
    val type: Int
)
