package hue

import com.natpryce.konfig.Configuration
import inkapplications.shade.auth.TokenStorage
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import config.*
import mu.KotlinLogging
import org.koin.core.get
import org.litote.kmongo.*

private val logger = KotlinLogging.logger{}

class MongoDBTokenStorage : TokenStorage, KoinComponent{
    val config : Configuration by inject()
    val databasename = config[dbname]

    private var cachedToken : String? = null

    override suspend fun getToken(): String? {
        if(cachedToken == null) cachedToken = getFromDB()
        return cachedToken
    }

    private fun getFromDB() : String? {
        logger.info { "Fetching token from db" }
        val client = get<MongoClient>()
        val db = client.getDatabase(databasename)
        val tokenCollection = db.getCollection<HueToken>()
        val token =  tokenCollection.find().first()

        client.close()
        return token?.token
    }

    private fun createDBEntry(token : String) {
        logger.info{ "Adding token to db" }
        val client = get<MongoClient>()
        val db = client.getDatabase(databasename)
        val tokenCollection = db.getCollection<HueToken>()

        tokenCollection.insertOne(HueToken(token))
        client.close()
    }

    override suspend fun setToken(token: String?) {
        cachedToken =
                if(token == null) null
                else {
                    createDBEntry(token)
                    token
                }
    }
}