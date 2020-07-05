import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationProperties
import config.*
import io.vertx.core.AsyncResult
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named
import io.vertx.core.Handler
import wmi.getWMIData

private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()

    val publish : (Map<String,String>) -> Unit by inject(named("publish"))
    val getPCStatMap : () -> Map<String,String> by inject(named("getPCStatMap"))

    fun infiniteLoop() {

        while (true) {
            if (!client.isConnected) {
                logger.info { "attempting to connect to broker..." }

                client.connect(config[ip], config[host], get(named("connectHandler")))
                client.publishHandler(get(named("publishHandler")))
            }

            //publish all stats about the pc for each loop
            publish(getPCStatMap())

            Thread.sleep(1000* config[pollingrate].toLong())
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