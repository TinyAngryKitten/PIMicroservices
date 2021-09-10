package homey

import com.natpryce.konfig.Configuration
import config.HomeyToken
import config.TokenStorage
import config.homeyip
import mu.KotlinLogging
import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.koin.java.KoinJavaComponent

private val logger = KotlinLogging.logger{}

private val sendRequest : HttpHandler = ApacheClient()
private val config : Configuration by KoinJavaComponent.inject(Configuration::class.java)

private val homeyAddress : String = config[homeyip]
private var homeyToken : String = fetchHomeyToken()

private fun fetchHomeyToken() =
    KoinJavaComponent.get<TokenStorage>(TokenStorage::class.java)
        .fetchToken<HomeyToken>(HomeyToken.defaultName)!!
        .token

fun invalidateHomeyToken() {
    logger.info { "Updating homey token..." }
    homeyToken = fetchHomeyToken()
}

fun sendHomeyRequest(method: Method, path : String, modifyRequest : Request.() -> Unit = {}) =
        sendRequest(
                Request(method, "http://${homeyAddress}/$path")
                        .header("Authorization", "Bearer $homeyToken")
                        .apply(modifyRequest)
        )