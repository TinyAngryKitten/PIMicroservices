package handlers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<MqttPublishMessage>, KoinComponent {


    override fun handle(event: MqttPublishMessage?) {
        val topic = event?.topicName()?:""
        val payload = event?.payload().toString()
        
    }
}