package homey

import com.mongodb.client.MongoClient
import com.natpryce.konfig.Configuration
import config.HomeyToken
import config.TokenStorage
import config.homeyip
import org.http4k.client.ApacheClient
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject

private val sendRequest : ApacheClient by inject(ApacheClient::class.java)
private val config : Configuration by inject(Configuration::class.java)

private val homeyAddress : String = config[homeyip]
private val homeyToken : String by lazy {
    get<TokenStorage>(TokenStorage::class.java)
        .fetchToken<HomeyToken>(HomeyToken.defaultName)!!
        .token
}

fun runFlow() {

}