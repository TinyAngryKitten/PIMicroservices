import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationProperties
import config.*
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()
    val db : TokenStorage by inject()

    fun infiniteLoop() {

        ApacheClient()(Request(Method.POST,"https://accounts.athom.com/login"))

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
