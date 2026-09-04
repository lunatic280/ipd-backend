package com.example.Ipd.naver.dto

data class NaverDirectionsResponse(
    val code: Int,
    val message: String,
    val route: Route
) {
    data class Route(
        val traoptimal: List<Traoptimal> = emptyList(),
        val trafast: List<Traoptimal> = emptyList(),
        val tracomfort: List<Traoptimal> = emptyList(),
    )

    data class Traoptimal(
        val guide: List<Guide>,
        val path: List<List<Double>>,   // [경도, 위도]
        val section: List<Section>,
        val summary: Summary
    )

    data class Guide(
        val distance: Int,
        val duration: Long,
        val instructions: String,
        val pointIndex: Int,
        val type: Int
    )

    data class Section(
        val congestion: Int,
        val distance: Int,
        val name: String,
        val pointCount: Int,
        val pointIndex: Int,
        val speed: Int
    )

    data class Summary(
        val bbox: List<List<Double>>,
        val departureTime: String,
        val distance: Int,
        val duration: Long,
        val fuelPrice: Int,
        val goal: Goal,
        val start: Start,
        val taxiFare: Int,
        val tollFare: Int
    )

    data class Goal(
        val dir: Int,
        val location: List<Double> // [경도, 위도]
    )

    data class Start(
        val location: List<Double> // [경도, 위도]
    )
}

fun NaverDirectionsResponse.toAppDto(): DirectionsForAppResponse {
    val route = this.route

    // 우선순위: traoptimal > trafast > tracomfort
    val bestList = when {
        route.traoptimal.isNotEmpty() -> route.traoptimal
        route.trafast.isNotEmpty()    -> route.trafast
        route.tracomfort.isNotEmpty() -> route.tracomfort
        else -> throw IllegalArgumentException(
            "No route found (traoptimal/trafast/tracomfort). code=$code, message=$message"
        )
    }

    val best = bestList.first()

    val path = best.path.map {
        coord ->
        require(coord.size == 2) { "Invalid coord: $coord"}
        val lon = coord[0]
        val lat = coord[1]
        LatLngDto(lat = lat, lng = lon)
    }
    val guides = best.guide.map { g ->
        GuideDto(
            distance = g.distance,
            duration = g.duration,
            instructions = g.instructions,
            pointIndex = g.pointIndex,
            type = g.type
        )
    }

    return DirectionsForAppResponse(
        code = this.code,
        message = this.message,
        distance = best.summary.distance,
        duration = best.summary.duration,
        path = path,
        guides = guides
    )
}
