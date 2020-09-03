import com.natpryce.konfig.Configuration
import config.host
import config.port
import config.konfigModule
import config.mainModule
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named
import io.vertx.ext.consul.ConsulClient
import io.vertx.ext.consul.ServiceOptions
import notifications.Notification
import notifications.NotificationSender
import notifications.TokenStorage
import notifications.discord.DiscordNotifications
import notifications.discord.DiscordToken
import java.net.InetAddress

private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()

    fun infiniteLoop() {

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

            Main().infiniteLoop()
        }
    }
}
