package com.example.Ipd.led.controller

import com.example.Ipd.led.dto.LedStatusDto
import com.example.Ipd.qr.util.MqttClientManager
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/led")
class LedController(
    private val mqtt: MqttClientManager,
    private val objectMapper: ObjectMapper
) {

    @PostMapping("/{deviceId}/change")
    fun changeLed(
        @PathVariable deviceId: String,
        @RequestBody dto: LedStatusDto
    ): ResponseEntity<LedStatusDto> {
        val topic = "bss/$deviceId/led"

        mqtt.publish(topic, objectMapper.writeValueAsString(dto))
        return ResponseEntity.accepted().build()
    }
}
