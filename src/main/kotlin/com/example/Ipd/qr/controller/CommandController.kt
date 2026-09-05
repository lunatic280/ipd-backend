// src/main/kotlin/com/example/Ipd/qr/controller/QrCommandController.kt
package com.example.Ipd.qr.controller

import com.example.Ipd.qr.dto.NextStepRequest
import com.example.Ipd.qr.dto.OpenSlotRequest
import com.example.Ipd.qr.dto.OpenSlotResponse
import com.example.Ipd.qr.util.MqttClientManager

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/qr")   // 👉 base path: /qr/...
class QrCommandController(
    private val mqtt: MqttClientManager
) {


    /**
     * 앱에서 호출하는 엔드포인트
     *
     * POST /qr/openBss
     * Body: { "bssId": "DEMO_BSS", "slot": 2 }
     */
    @PostMapping("/openBss")
    fun openBss(
        @RequestBody req: OpenSlotRequest
    ): ResponseEntity<OpenSlotResponse> {


        val topic = "/server/cmd"
        val payload = "OPEN:${req.bssId}:${req.slot}"   // 예: OPEN:DEMO_BSS:2

        return try {
            mqtt.publish(topic, payload)

            ResponseEntity.ok(
                OpenSlotResponse(
                    success = true,
                    message = "BSS ${req.bssId} 의 ${req.slot}번 슬롯 오픈 명령 전송"
                )
            )
        } catch (e: Exception) {

            // 여기서는 HTTP 200 + success=false 로만 내려가도록 처리
            ResponseEntity.ok(
                OpenSlotResponse(
                    success = false,
                    message = "장치 명령을 전송하지 못했습니다."
                )
            )
        }
    }

    @PostMapping("/nextStep")
    fun nextStep(
        @RequestBody request: NextStepRequest
    ): ResponseEntity<OpenSlotResponse> {

        val topic = "/server/cmd"
        val payload = "NEXT:${request.bssId}"

        return try {
            mqtt.publish(topic, payload)

            ResponseEntity.ok(
                OpenSlotResponse(
                    success = true,
                    message = "BSS ${request.bssId} NEXT 명령 전송"
                )
            )
        } catch (e: Exception) {

            ResponseEntity.ok(
                OpenSlotResponse(
                    success = false,
                    message = "장치 명령을 전송하지 못했습니다."
                )
            )
        }
    }


}
