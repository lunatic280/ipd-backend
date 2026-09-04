package com.example.Ipd.naver.dto

data class ReverseGeocodeResponse(
    val status: Status,
    val results: List<ResultItem>
)

data class Status(
    val code: Int,
    val name: String,
    val message: String
)

data class ResultItem(
    val name: String,        // "roadaddr" / "addr"
    val code: Code,
    val region: Region,
    val land: Land
)

data class Code(
    val id: String,
    val type: String,
    val mappingId: String
)

data class Region(
    val area0: Area?,
    val area1: Area,
    val area2: Area,
    val area3: Area,
    val area4: Area?
)

data class Area(
    val name: String,
    val alias: String? = null
)

data class Land(
    val type: String?,
    val number1: String,
    val number2: String?,
    val addition0: Addition?,
    val addition1: Addition?,
    val addition2: Addition?,
    val addition3: Addition?,
    val addition4: Addition?
)

data class Addition(
    val type: String,
    val value: String
)
