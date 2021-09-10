package handlers

import data.Variable
import homey.fetchVariable
import homey.publishUpdatedVariable
import homey.runFlow
import homey.updateState
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<MqttPublishMessage>, KoinComponent {

    override fun handle(event: MqttPublishMessage?) {
        val topic = event?.topicName()?:return
        val payload = event.payload().toString()

        logger.info { "owo" }

        when {
            topic.startsWith("action") -> runFlow(topic.takeLastWhile { it != '/' })
            topic == "state/get" -> publishUpdatedVariable(fetchVariable(payload))
            topic.startsWith("state/update/boolean/") -> publishUpdatedVariable(
                    updateState(topic.takeLastWhile { it != '/' }, payload.equals("true", true))
            )
            else -> logger.error { "received message on unknown topic ($topic): $payload" }
        }
    }
}