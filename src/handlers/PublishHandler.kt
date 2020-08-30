package handlers

import hue.*
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<MqttPublishMessage>, KoinComponent {
    val hueController: HueController by inject()
    val client: MqttClient by inject()

    override fun handle(event: MqttPublishMessage?) {
        val topicParts = event?.topicName()?.split("/") ?: return
        val payload = event.payload().toString()

        val group = topicParts[2]
        val command = topicParts[3]

        when (command) {
            "state" -> {
                logger.info { "updating state of $group to $payload" }
                hueController.toggleGroup(HueName(group), payload.equals("on", true))
                broadcastChanges(topicParts, group)
            }
            "brightness" -> {
                logger.info { "updating brightness of $group to $payload" }
                hueController.changeBrightnessOfGroup(HueName(group), payload.toInt())
                broadcastChanges(topicParts, group)
            }
            "color" -> {
                logger.info { "updating color of $group to $payload" }
                hueController.changeColorOfGroup(HueName(group))
                broadcastChanges(topicParts, group)
            }
            "update" -> broadcastGroupState(event.topicName(), group)
            else -> logger.error { "message received on unrecognized topic: ${event.topicName()}" }
        }
    }

    private fun broadcastChanges(topicParts: List<String>, group: String) =
        broadcastGroupState("${topicParts[0]}/${topicParts[1]}/${topicParts[2]}/state", group)

    private fun broadcastGroupState(baseTopic: String, group: String) {
        logger.info { "Broadcasting group state of group: $group" }
        val state = hueController.getStateOfGroup(HueName(group))

        val brightness = (state.brightness as? GroupBrightness.CommonBrightness)?.brightness ?: 0
        val onState = (state.onState as? GroupOnState.CommonOnState)?.isOn ?: false

        client.publish(
            "$baseTopic/brightness",
            Buffer.buffer(if(onState) brightness.toString() else "0"),
            MqttQoS.AT_MOST_ONCE,
            false,
            false
        )

        client.publish(
            "$baseTopic/state",
            Buffer.buffer(onState.toString()),
            MqttQoS.AT_MOST_ONCE,
            false,
            false
        )
        
        /*client.publish(
            "$baseTopic/color",
            Buffer.buffer( (state.color as? GroupColor.CommonColor)?. ?: 0),
            MqttQoS.AT_MOST_ONCE,
            false,
            false
        )*/
    }
}
