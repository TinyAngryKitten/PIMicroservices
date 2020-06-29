package handlers

import io.vertx.core.AsyncResult
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : (AsyncResult<MqttPublishMessage>) -> Unit {
    override fun invoke(msg: AsyncResult<MqttPublishMessage>) {
        logger.info { "Received message on topic: ${msg.result().topicName()}" }
    }
}