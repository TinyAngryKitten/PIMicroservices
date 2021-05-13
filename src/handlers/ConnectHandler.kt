package handlers

import actions.IntervalAction
import actions.MqttAction
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.messages.MqttConnAckMessage
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import kotlin.reflect.full.primaryConstructor

private val logger = KotlinLogging.logger {}

object SimpleConnectHandler : Handler<AsyncResult<MqttConnAckMessage>>, KoinComponent {
    val topics : List<String> =
        MqttAction::class
            .sealedSubclasses
            .mapNotNull {
                it.objectInstance?.topic
            }
    val client : MqttClient by inject()

    override fun handle(event: AsyncResult<MqttConnAckMessage>?) {
        logger.info { "Connection established to broker" }

        topics.forEach { println("Subscribing to topic: ${it}") }

        topics.forEach { client.subscribe(it, 2) }
    }
}