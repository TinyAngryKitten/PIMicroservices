package handlers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.messages.MqttConnAckMessage
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object SimpleConnectHandler : Handler<AsyncResult<MqttConnAckMessage>> {
    override fun handle(event: AsyncResult<MqttConnAckMessage>?) {
        logger.info { "Connection established to broker" }
    }
}