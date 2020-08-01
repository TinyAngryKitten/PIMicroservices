package notifications

import com.mongodb.client.MongoClient
import com.natpryce.konfig.Configuration
import config.dbname
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection


private val logger = KotlinLogging.logger{}
open class DiscordTokenStorage : KoinComponent {

  val config : Configuration by inject()
  open val databasename = config[dbname]

   open fun fetchTokenFromDb(name : String) : DiscordToken? {
    logger.info { "Fetching item from db" }
    val client = get<MongoClient>()
    val db = client.getDatabase(databasename)
    val tokenCollection = db.getCollection<DiscordToken>()
    val item =  tokenCollection.findOne(DiscordToken::hookName eq name)

    return item
  }

   open fun addToken(token : DiscordToken) {
    logger.info{ "Adding item to db" }
    val client = get<MongoClient>()
    val db = client.getDatabase(databasename)
    val tokenCollection = db.getCollection<DiscordToken>()

    tokenCollection.insertOne(token)
  }
}