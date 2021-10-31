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
import io.vertx.ext.consul.ConsulClient
import io.vertx.ext.consul.ServiceOptions
import io.vertx.core.Vertx

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
