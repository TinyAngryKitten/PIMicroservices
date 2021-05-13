package actions

import com.natpryce.konfig.Configuration
import config.HomeyToken
import config.TokenStorage
import config.homeyip
import io.vertx.core.Vertx
import mu.KotlinLogging
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.KoinComponent
import org.koin.core.inject


private val logger = KotlinLogging.logger{}

object FlowTrigger : MqttAction(), KoinComponent {
    override val topic: String
        get() = "homey/flows/run"

    private val config : Configuration by inject()
    private val vertx : Vertx by inject()
    var web = OkHttpClient()

    private val storage = TokenStorage()
    private val token = storage.fetchToken<HomeyToken>(HomeyToken.secretName)?.value

    private val ip = config[homeyip]

    private val baseUrl = "http://$ip/api/app/com.internet/"

    override fun performAction(payload: String, topic: String) {
        val url = baseUrl+payload
        logger.info { "Triggering flow on url: $url" }

        val response = web.newCall(Request.Builder()
            .get()
            .url(url)
            .addHeader("Authorization", "Bearer " + token)
            .build())
            .execute()

            if(response.isSuccessful) logger.info { "Flow at $url triggered successfully" }
            else logger.error {
                "Flow at $url could not be triggered due to: ${response.message}"
            }
    }
}