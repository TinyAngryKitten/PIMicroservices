package handlers

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging
import notifications.Notification
import notifications.NotificationSender
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<MqttPublishMessage>, KoinComponent {
    private val notificationSender : NotificationSender by inject()
    private val mapper : ObjectMapper by inject()

    override fun handle(event: MqttPublishMessage?) {
        val topic = event?.topicName()?:return
        val payload = event.payload().toString()

        logger.info { "Notification received on $topic:\n $payload" }

        val notification = mapper.readValue(payload,Notification::class.java)

        if(notification != null) notificationSender.notify(notification)
    }
}