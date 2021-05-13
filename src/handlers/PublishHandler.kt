package handlers

import actions.MqttAction
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.mqtt.messages.MqttPublishMessage
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.reflect.full.primaryConstructor

private val logger = KotlinLogging.logger {}

object SimplePublishHandler : Handler<MqttPublishMessage>, KoinComponent {

    val actions : List<MqttAction> =
        MqttAction::class
            .sealedSubclasses
            .mapNotNull {
                it.objectInstance
            }

    override fun handle(event: MqttPublishMessage?) {
        val topic = event?.topicName()?:return
        val payload = event.payload().toString()

        actions.forEach{ action ->
            if (action matches topic) action.performAction(payload, topic)
        }
    }

    private infix fun MqttAction.matches(topic : String) : Boolean =
            matches(topic.split("/"), topic.split("/"))

    private fun matches(messageTopic : List<String>, actionTopic : List<String>) : Boolean =
        if(messageTopic.isEmpty() && actionTopic.isEmpty()) true
        else if(messageTopic.first() == actionTopic.first() || actionTopic.first() == "+") matches(messageTopic.drop(1), actionTopic.drop(1))
        else actionTopic.first() == "#"
}