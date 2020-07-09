package handlers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.messages.MqttConnAckMessage
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

private val logger = KotlinLogging.logger {}

object SimpleConnectHandler : Handler<AsyncResult<MqttConnAckMessage>>, KoinComponent {
    val topics: Map<String,Int> by inject(named("topics"))
    val client : MqttClient by inject()

    override fun handle(event: AsyncResult<MqttConnAckMessage>?) {
        logger.info { "Connection established to broker" }

        topics.forEach{client.subscribe(it.key,it.value) }
    }
}