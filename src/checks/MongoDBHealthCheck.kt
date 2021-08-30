package checks

import com.mongodb.client.MongoClient
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger{}
object MongoDB : HealthCheck(), KoinComponent {
    val mongodb : MongoClient by inject()
    val expectedDbNames = listOf(
        "notifications",
        "actions",
        "hue",
    )

    override fun invoke() {
        try {
                mongodb
                    .listDatabaseNames()
                    .filter { it in expectedDbNames }
                    .also { databasesFound ->
                        databasesFound.forEach { recordHealth(it, true)}
                        expectedDbNames.filterNot { it in databasesFound }
                            .forEach { recordHealth(it,false) }
                    }
            logger.info { "haelth checked" }
        } catch (e : Exception) {
            logger.error(e) {"An error occured while querying mongodb"}
            recordHealth(false)
            expectedDbNames.forEach { recordHealth(it,false) }
        }
    }
}