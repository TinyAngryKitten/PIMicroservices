package handlers

import io.vertx.core.AsyncResult
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object SimpleDisconnectHandler : (AsyncResult<Void>) -> Unit {
    override fun invoke(arg: AsyncResult<Void>) {
        logger.info { "Disconnected from broker..." }
    }
}