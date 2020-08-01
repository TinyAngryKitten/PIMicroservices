package notifications.discord

import arrow.syntax.function.memoize
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

   open val fetchTokenFromDb =
        {
          name: String ->
          logger.info { "Fetching token $name from db" }
          val client = get<MongoClient>()
          val db = client.getDatabase(databasename)
          val tokenCollection = db.getCollection<DiscordToken>()
          val item =  tokenCollection.findOne(DiscordToken::hookName eq name)

          item
        }.memoize()

   open fun addToken(token : DiscordToken) {
    logger.info{ "Adding token ${token.hookName} to db" }
    val client = get<MongoClient>()
    val db = client.getDatabase(databasename)
    val tokenCollection = db.getCollection<DiscordToken>()

    tokenCollection.insertOne(token)
  }
}