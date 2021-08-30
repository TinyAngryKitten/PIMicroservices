import checks.HealthCheck
import checks.MongoDB
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
import io.vertx.core.Vertx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

private val logger = KotlinLogging.logger{}

@ExperimentalTime
class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()
    val vertx : Vertx by inject()

    val healthCheckInterval = Duration.ofSeconds(10)
    val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    val healthChecks = HealthCheck::class
        .sealedSubclasses
        .mapNotNull { it.objectInstance }

    fun infiniteLoop() {

        vertx.setPeriodic(healthCheckInterval.toMillis()) {
            logger.info { "DOING THING" }
            MongoDB()
            logger.info { "DONE THING" }
            healthChecks.forEach { healthCheck ->
                println("for each")
                logger.info {"for each"}
                coroutineScope.launch {
                    logger.info {"health checking"}
                    println("health checking")
                    healthCheck()
                }
            }
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

            Main().infiniteLoop()
        }
    }
}
