package handlers

import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class SimplePublisher : KoinComponent {
    val client : MqttClient by inject()
    val baseTopic : String by inject(named("baseTopic"))

    fun publish(messageMap : Map<String,String>) {
        messageMap.entries.forEach {
            client.publish(baseTopic + it.key, Buffer.buffer(it.value),MqttQoS.AT_MOST_ONCE,false,false)
        }
    }
}