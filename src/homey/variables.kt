package homey

import data.Variable
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.http4k.core.Method
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject

private val logger = KotlinLogging.logger{}
private val json : Json by KoinJavaComponent.inject(Json::class.java)
val client : MqttClient by inject(MqttClient::class.java)

private val variables : List<Variable>
    get() = sendHomeyRequest(Method.GET, "api/manager/logic/variable")
            .bodyString()
            .let {json.decodeFromString<Map<String, Variable>>(it) }
            .values
            .toList()

fun fetchVariable(name : String) : Variable? =
        variables.find {
            it.name == name
        }

fun updateState(name : String, newState : Boolean) : Variable? =
        fetchVariable(name)
                ?.let { variable ->
                    sendHomeyRequest(Method.PUT, "api/manager/logic/variable/${variable.id}") {
                        body("{ \"value\" : ${if(!newState) "true" else "false" }")
                    }
                }?.bodyString()
                ?.let { json.decodeFromString(it) }

fun publishUpdatedVariable(variable : Variable?) {
    if(variable != null) client.publish(
            "state/status/${variable.name}",
            Buffer.buffer(variable.value.toString()),
            MqttQoS.AT_LEAST_ONCE,
            false,
            true
    )
}