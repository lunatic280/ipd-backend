package com.example.Ipd.qr.util

import com.hivemq.client.mqtt.MqttClient
import org.slf4j.LoggerFactory
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MqttClientManager(
    @Value("\${mqtt.enabled:false}") private val enabled: Boolean,
    @Value("\${mqtt.host:example.invalid}") private val host: String,
    @Value("\${mqtt.port:1883}") private val port: Int
) {
    // 전시용 구조만 보존합니다. 실장치 연결에는 TLS, broker 인증과 topic ACL이 필요합니다.
    private val logger = LoggerFactory.getLogger(MqttClientManager::class.java)

    private val client by lazy {
        MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(port)
            .buildAsync()
    }


    @PostConstruct
    fun init() {
        if (!enabled) {
            return
        }


        client.connect().whenComplete { _, ex ->
            if (ex == null) {
                subscribeTopics()
            } else {
                logger.error("MQTT connection failed")
            }
        }
    }

    private fun subscribeTopics() {
        client.subscribeWith().topicFilter("/esp/+/sensor").send()
        client.subscribeWith().topicFilter("/esp/+/status").send()
        client.subscribeWith().topicFilter("/esp/+/led").send()
    }

    fun publish(topic: String, message: String) {
        check(enabled) { "전시본에서는 MQTT 전송이 비활성화되어 있습니다." }

        client.publishWith()
            .topic(topic)
            .payload(message.toByteArray())
            .send()
    }
}
