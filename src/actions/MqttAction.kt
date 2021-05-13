package actions

import io.vertx.mqtt.messages.MqttMessage

sealed class MqttAction() {
    abstract val topic : String
    abstract fun performAction(payload : String, topic : String)
}