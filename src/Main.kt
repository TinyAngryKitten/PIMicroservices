import com.natpryce.konfig.Configuration
import config.*
import homey.invalidateHomeyToken
import homey.publishAllVariables
import homey.updateHomeyToken
import io.vertx.core.Vertx
import io.vertx.mqtt.MqttClient
import mu.KotlinLogging
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
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
    val vertx: Vertx by inject()

    fun startHealthChecks() {
        { request : Request ->
            updateHomeyToken(request.bodyString())
            Response(Status(if(isHealthy()) 200 else 500,""))
        }.asServer(Netty(config[healthport])).start()
    }

    private fun isHealthy() : Boolean {
        return client.isConnected
    }

    fun infiniteLoop() {

        { request : Request ->
            updateHomeyToken(request.bodyString())
            Response(OK)
        }.asServer(Netty(config[httpPort])).start()

        logger.info { "Listening for new tokens at ${config[httpPort]}" }

        vertx.setPeriodic(config[updateInterval].toLong() * 1000) {
            invalidateHomeyToken()
            publishAllVariables()
        }

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
