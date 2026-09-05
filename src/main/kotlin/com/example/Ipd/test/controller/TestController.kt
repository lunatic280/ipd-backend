package com.example.Ipd.test.controller

import com.example.Ipd.test.dto.SensorDto

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class TestController {


    @PostMapping("/test")
    fun changeInt(@RequestBody sensorDto: SensorDto): ResponseEntity<SensorDto> {
        return kotlin.runCatching {
            ResponseEntity.ok().body(sensorDto)
        }.getOrElse { exception ->
            ResponseEntity.badRequest().build()
        }
    }
}
