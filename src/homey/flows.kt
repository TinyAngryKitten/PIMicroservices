package homey

import com.natpryce.konfig.Configuration
import config.HomeyToken
import config.TokenStorage
import config.homeyip
import data.Flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.http4k.core.*
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject

private val logger = KotlinLogging.logger{}

private val json = Json { ignoreUnknownKeys = true }

private val flows : List<Flow>
    get() = sendHomeyRequest(Method.GET, "api/manager/flow/flow")
            .bodyString()
            .let {json.decodeFromString<Map<String, Flow>>(it) }
            .values
            .toList()

fun runFlow(flowName : String) =
        getFlowByName(flowName)
                ?.id
                ?.let { id -> sendTriggerRequest(id, flowName) }
                ?: logger.error { "request to trigger unkown flow $flowName received" }

fun sendTriggerRequest(id : String, flowName: String) =
    sendHomeyRequest(Method.POST, "api/manager/flow/flow/$id/trigger")
            .takeIf { it.status != Status.OK }
            ?.also { logger.error { "Error encountere when attempting to trigger $flowName: ${it.status}, ${it.bodyString()}" } }
            ?: logger.info { "Triggered the flow $flowName" }

fun getFlowByName(name : String) = flows.find {
    it.name == name
}