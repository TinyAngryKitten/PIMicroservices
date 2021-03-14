package config

import arrow.syntax.function.memoize
import com.mongodb.ConnectionString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.natpryce.konfig.Configuration
import handlers.SimpleConnectHandler
import handlers.SimpleDisconnectHandler
import handlers.SimplePublishHandler
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.consul.CheckOptions
import io.vertx.ext.consul.ConsulClient
import io.vertx.ext.consul.ConsulClientOptions
import io.vertx.ext.consul.ServiceOptions
import io.vertx.ext.web.client.WebClient
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.MqttClientOptions
import notifications.discord.DiscordNotifications
import notifications.NotificationSender
import notifications.TokenStorage
import notifications.discord.DiscordToken
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.litote.kmongo.KMongo
import java.net.InetAddress

val serviceName = "notifications"

val mainModule = module {
    single{
        MqttClientOptions().apply {
            isAutoKeepAlive = true
            isAutoGeneratedClientId = true
        }
    }

    single { ObjectMapper().registerModule(KotlinModule()) }

    single { Vertx.vertx() }

    single {
        MqttClient.create(
            get(),
            get()
        ) as MqttClient
    }

    single {
        val config : Configuration by inject()

        val user = config[dbuser]
        val password = config[dbpassword]
        val host = config[dburl]
        val port = config[dbport]

        ConnectionString(
            "mongodb://$user:$password@$host:$port/?authSource=admin&readPreference=primary"
        )
    }

    factory {
        KMongo.createClient(get<ConnectionString>())
    }

    single {
        val config = get<Configuration>()
        ConsulClientOptions().apply {
            host = config[consulhost]
            port = config[consulport]
    } }
    
    single<ConsulClient> { ConsulClient.create(get(),get())}

    single {
        ServiceOptions()
        .setName(serviceName)
            .setAddress(InetAddress.getLocalHost()?.hostAddress?:"")
        //.setId("serviceId")
        .setTags(listOf("mqtt", "vertx"))
        .setCheckOptions(CheckOptions().setTtl("60s"))
        .setPort(6669)
    }

    factory { WebClient.create(get()) }

    single(named("disconnectHandler")) { SimpleDisconnectHandler as Handler<*>}
    single(named("connectHandler")) { SimpleConnectHandler as Handler<*>}
    single(named("publishHandler")) { SimplePublishHandler as Handler<*>}

    single { TokenStorage() }

    single {OkHttpClient()}
    single{ get<TokenStorage>().fetchToken<DiscordToken>("General")!! }
    single(named("fetchDiscordToken")){ { name: String -> get<TokenStorage>().fetchToken<DiscordToken>(name)}.memoize() }

    //add topics to subscribe to
    single(named("topics")) {
        mapOf<String,Int>(
            "notify" to MqttQoS.EXACTLY_ONCE.value(),
            "notify/+"  to MqttQoS.EXACTLY_ONCE.value()
        )
    }
}