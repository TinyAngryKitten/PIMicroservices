package homey

import config.HomeyToken
import config.TokenStorage
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.Exception

private val logger = KotlinLogging.logger{}

object updateHomeyToken : (String) -> Unit, KoinComponent {
    private val mongoClient : TokenStorage by inject()

    override fun invoke(token: String) {
        try {
            logger.info { "Removing old homey token..." }
            mongoClient.removeToken<HomeyToken>(HomeyToken.defaultName)
        }catch (e : Exception) {}

        logger.info { "Adding new homey token... $token" }
        mongoClient.addToken(HomeyToken(token = token))
    }
}