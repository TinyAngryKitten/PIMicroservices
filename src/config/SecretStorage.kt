package config

import com.mongodb.client.MongoClient
import com.natpryce.konfig.Configuration
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection


private val logger = KotlinLogging.logger{}

open class TokenStorage : KoinComponent {
  val config : Configuration by inject()
  open val databasename = config[dbname]

  inline fun <reified T: Secret> fetchToken(name : String) : T? {
    logAction("fetching token: $name of type: ${T::class.simpleName}")
    val client = get<MongoClient>()
    val db = client.getDatabase(databasename)
    val tokenCollection = db.getCollection<T>()

    return tokenCollection.findOne(Secret::name eq name)
  }

  fun logAction(msg: String)= logger.info {msg}

  inline fun <reified T: Secret> addToken(token : T) {
    logAction("Adding token: ${token.name} of type ${token::class.simpleName}")
    val client = get<MongoClient>()
    val db = client.getDatabase(databasename)
    val tokenCollection = db.getCollection<T>()

    tokenCollection.insertOne(token)
  }
}