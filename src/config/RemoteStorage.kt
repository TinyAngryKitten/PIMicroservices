package config

import com.mongodb.client.MongoClient
import com.natpryce.konfig.Configuration
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.litote.kmongo.getCollection


private val logger = KotlinLogging.logger{}
class RemoteStorage : KoinComponent {

  val config : Configuration by inject()
  val databasename = config[dbname]

  private fun fetchItemFromDb() : String? {
    logger.info { "Fetching item from db" }
    val client = get<MongoClient>()
    val db = client.getDatabase(databasename)
    val tokenCollection = db.getCollection<String>()
    val item =  tokenCollection.find().first()

    client.close()
    return item
  }

  private fun createDBEntry(item : String) {
    logger.info{ "Adding item to db" }
    val client = get<MongoClient>()
    val db = client.getDatabase(databasename)
    val tokenCollection = db.getCollection<String>()

    tokenCollection.insertOne(item)
    client.close()
  }
}