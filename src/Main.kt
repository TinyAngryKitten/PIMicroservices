import actions.IntervalAction
import actions.MqttAction
import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationProperties
import config.host
import config.port
import config.konfigModule
import config.mainModule
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
import kotlin.reflect.full.primaryConstructor
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger{}

class Main : KoinComponent {
    val config : Configuration by inject()
    val client: MqttClient by inject()
    val vertx : Vertx by inject()
    val actions : List<IntervalAction> =
        IntervalAction::class
            .sealedSubclasses
            .mapNotNull {
                it.primaryConstructor?.call()
            }

    @ExperimentalTime
    fun infiniteLoop() {

        actions.forEach {
            vertx.setPeriodic(it.interval.toLongMilliseconds()) {
                it.performAction()
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
        @ExperimentalTime
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
