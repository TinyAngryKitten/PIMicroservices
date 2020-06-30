package config

import handlers.SimpleConnectHandler
import handlers.SimpleDisconnectHandler
import handlers.SimplePublishHandler
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.MqttClientOptions
import io.vertx.mqtt.messages.MqttConnAckMessage
import org.koin.core.qualifier.named
import org.koin.dsl.module
import wakeonlan.WoLClient

val mainModule = module {
    single{
        MqttClientOptions().apply {
            isAutoKeepAlive = true
            isAutoGeneratedClientId = true
        }
    }

    single {
        MqttClient.create(
            Vertx.vertx(),
            get()
        ) as MqttClient
    }

    single(named("disconnectHandler")) { SimpleDisconnectHandler as Handler<*>}
    single(named("connectHandler")) { SimpleConnectHandler as Handler<*>}
    single(named("publishHandler")) { SimplePublishHandler as Handler<*>}

    single{WoLClient()}

    //add topics to subscribe to
    single(named("topics")) {
        mapOf(
            "computers/+/power" to MqttQoS.AT_MOST_ONCE.value()
        )
    }
}