package handlers

import hue.HueController
import hue.HueName
import io.vertx.core.Handler
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<MqttPublishMessage>, KoinComponent {
    val hueController : HueController by inject()
    val client : MqttClient by inject()

    override fun handle(event: MqttPublishMessage?) {
        val topicParts = event?.topicName()?.split("/")?:return
        val payload = event.payload().toString()

        val group = topicParts[2]
        val command = topicParts[3]

        when(command) {
            "state" -> {
                logger.info{"updating state of $group to $payload"}
                hueController.toggleGroup(HueName(group),payload.equals("on",true))
            }
            "brightness" -> {
                logger.info{"updating brightness of $group to $payload"}
                hueController.changeBrightnessOfGroup(HueName(group),payload.toInt())
            }
            "color" -> {
                logger.info{"updating color of $group to $payload"}
                hueController.changeColorOfGroup(HueName(group))
            }
            "update" -> broadcastGroupState(group)
            else -> logger.error {"message received on unrecognized topic: ${event.topicName()}"}
        }
    }

    private fun broadcastGroupState(group : String) {
        logger.info{"Broadcasting group state of group: $group"}
        val state = hueController.getStateOfGroup( HueName(group) )
    }
}