package handlers

import io.vertx.core.AsyncResult
import io.vertx.mqtt.messages.MqttConnAckMessage
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object SimpleConnectHandler : (AsyncResult<MqttConnAckMessage>) -> Unit {
    override fun invoke(p1: AsyncResult<MqttConnAckMessage>) {
        logger.info { "Connection established to broker" }
    }

}