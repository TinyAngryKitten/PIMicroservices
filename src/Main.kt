import com.natpryce.konfig.Configuration
import config.host
import config.ip
import config.konfigModule
import config.mainModule
import hue.HueController
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named

private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()
    val hueController : HueController by inject()

    fun infiniteLoop() {

//        hueController.init()

        while (true) {
            if (!client.isConnected) {
                logger.info { "attempting to connect to broker..." }

                client.connect(config[ip], config[host], get(named("connectHandler")))
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