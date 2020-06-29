package handlers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<AsyncResult<MqttPublishMessage>> {
    override fun handle(event: AsyncResult<MqttPublishMessage>?) {
        logger.info { "Received message on topic: ${event?.result()?.topicName()}" }
    }
}