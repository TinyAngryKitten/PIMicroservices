package handlers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object SimpleDisconnectHandler : Handler<AsyncResult<Void>> {
    override fun handle(event: AsyncResult<Void>?) {
        logger.info { "Disconnected from broker..." }
    }
}