package checks

import com.influxdb.client.InfluxDBClientOptions
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import com.natpryce.konfig.Configuration
import config.host
import config.influxorg
import config.influxtoken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.time.Instant

private val logger = KotlinLogging.logger{}
sealed class HealthCheck : () -> Unit, KoinComponent {
    private val config : Configuration by inject()

    protected val db : InfluxDBClientKotlin
        get() = InfluxDBClientKotlinFactory.create(InfluxDBClientOptions.builder()
            .bucket("services/autogen")
            .url("http://${config[host]}:8086")
            .authenticateToken(config[influxtoken].toCharArray())
            .org(config[influxorg])
            .build())

    protected val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    protected fun recordHealth(isUp : Boolean) = recordHealth(this::class.simpleName, isUp)
    protected fun recordHealth(serviceName : String?, isUp : Boolean) = coroutineScope.launch {
        logger.info { "starting" }
        db.use {

            it.getWriteKotlinApi()
                .writePoint(
                    Point
                        .measurement("health")
                        .addTag("service", this@HealthCheck::class.simpleName)
                        .addField("isUp", isUp)
                        .time(Instant.now().toEpochMilli(), WritePrecision.MS)
                )
        }
    }
}