package handlers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import wakeonlan.MachineID
import wakeonlan.WoLClient

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<MqttPublishMessage>, KoinComponent {
    val machines : List<MachineID> by inject()
    val client : WoLClient by inject()

    override fun handle(event: MqttPublishMessage?) {
        val topic = event?.topicName()?:""
        val payload = event?.payload().toString()

        if(topic.isEmpty() || payload != "on") return logger.error {"Invalid message received: $payload on topic: $topic"}

        val computerName = topic.split("/")[1]
        val machine = machines.find { it.name.equals(computerName,true) }
            ?: return logger.error { "machine with name $computerName not found"  }

        client.wake(machine.macAddress,machine.host)
        logger.info{"wake message sent to $machine"}
    }
}