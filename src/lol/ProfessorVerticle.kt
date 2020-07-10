package lol

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.ext.web.codec.BodyCodec

class ProfessorVerticle : AbstractVerticle() {

    val request = WebClient.create(vertx) // (1)
            .get(443, "https://porofessor.gg", "/live/euw/tinyangrykitten")
            .ssl(true)
            .putHeader("Accept", "application/json")
            .`as`(BodyCodec.string())
            .expect(ResponsePredicate.SC_OK)

    override fun start() {
        //"https://porofessor.gg/live/euw/tinyangrykitten"
        // The summoner is not in-game, please retry later. The game must be on the loading screen or it must have started.
        vertx.setPeriodic(3000) { _ -> fetchJoke() }
    }

    fun fetchJoke() {
        request.send { asyncResult ->
            if (asyncResult.succeeded()) {
                val isIngame = asyncResult.result().body().contains("The summoner is not in-game, please retry later")
                
            }
        }
    }
}