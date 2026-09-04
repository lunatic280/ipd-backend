package com.example.Ipd.naver.dto

data class SimpleAddress(
    val sido: String,
    val sigungu: String,
    val dong: String,
    val building: String
)

fun ReverseGeocodeResponse.toSimpleAddress(): SimpleAddress? {
    // results 가 null 이거나 빈 리스트면 바로 null
    val list = this.results
    print(results)
    if (list.isNullOrEmpty()) {
        return null
    }

    // roadaddr 우선, 없으면 addr 사용
    val target = list.firstOrNull { it.name == "roadaddr" }
        ?: list.firstOrNull { it.name == "addr" }
        ?: return null

    val region = target.region
    val land = target.land

    val sido = region.area1.name
    val sigungu = region.area2.name
    val dong = region.area3.name

    // building 은 없을 수도 있으니 Optional
    val building = land.addition0
        ?.takeIf { it.type == "building" }
        ?.value
        ?: ""

    return SimpleAddress(
        sido = sido,
        sigungu = sigungu,
        dong = dong,
        building = building
    )
}
