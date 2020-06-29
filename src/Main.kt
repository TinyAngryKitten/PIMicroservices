import com.natpryce.konfig.ConfigurationProperties
import config.host
import config.ip
import config.konfigModule
import config.mainModule
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named

private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config: ConfigurationProperties by inject()
    val topics: Map<String,Int> by inject(named("topics"))
    val client: MqttClient by inject()

    fun infiniteLoop() {
        logger.info { "Starting" }
        while (true) {
            if (!client.isConnected) {
                logger.info { "attempting to connect to broker..." }

                client.connect(config[ip], config[host], get(named("connectHandler")))
                client.disconnect(get(named("disconnectHandler")))
                client.publishHandler(get(named("publishHandler")))

                client.subscribe(topics)
            }

            Thread.sleep(10000)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
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