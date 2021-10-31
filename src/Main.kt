import com.natpryce.konfig.Configuration
import config.*
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import com.natpryce.konfig.ConfigurationProperties
import io.vertx.core.AsyncResult
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import notifications.Notification
import notifications.TokenStorage
import notifications.discord.DiscordNotifications
import notifications.discord.DiscordToken
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named


private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()
    val vertx: Vertx by inject()

    fun startHealthChecks() {
        vertx.createHttpServer().requestHandler { request ->
            request.response()
                .setStatusCode(if(isHealthy()) 200 else 500)
                .end()
        }.listen(config[healthport])
    }

    private fun isHealthy() : Boolean {
        return client.isConnected
    }

    fun infiniteLoop() {
        val httpServer = vertx.createHttpServer()

        httpServer.requestHandler { request ->
            request.bodyHandler {
                if(it != null && it.toString().isNotEmpty()) DiscordNotifications().notify(
                        Notification(body = it.toString() )
                )
            }
        }

        httpServer.listen(config[httpPort])

        logger.info { "Listening for messages at ${config[httpPort]}" }

        while (true) {
            if (!client.isConnected) {
                logger.info { "attempting to connect to broker..." }

                client.connect(config[port], config[host], get(named("connectHandler")))
                client.publishHandler(get(named("publishHandler")))
            }

            Thread.sleep(10000)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            logger.info { "Started" }
            startKoin {
                modules(
                        mainModule,
                        konfigModule
                )
            }

            Main().apply {
                startHealthChecks()
                infiniteLoop()
            }
        }
    }
}
