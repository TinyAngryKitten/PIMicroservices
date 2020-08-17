import com.natpryce.konfig.Configuration
import config.*
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named
import io.vertx.ext.consul.ConsulClient
import io.vertx.ext.consul.ServiceOptions
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import lol.ProfessorWatcher

private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()

    val consulClient : ConsulClient by inject()
    val consulOptions : ServiceOptions by inject()
    val vertx : Vertx by inject()

    fun infiniteLoop() {

        consulClient.registerService(consulOptions) {
            if(it.succeeded()) logger.info { "Service registered in consul" }
            else logger.error{"Service could not be registered in consul: ${it.cause()}"}
        }
        
        ProfessorWatcher().start()

        while (true) {
            if (!client.isConnected) {
                logger.info { "attempting to connect to broker..." }

                client.connect(config[port], config[host], get(named("connectHandler")))
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
