package lol
import com.natpryce.konfig.Configuration
import config.users
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.mqtt.MqttClient
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger{}

class ProfessorWatcher :  KoinComponent {
    val config : Configuration by inject()
    val usersString : String = config[users]
    val mqttClient : MqttClient by inject()
    val vertx : Vertx by inject()

    val pollingRate = 60000L

    lateinit var loopJob : Job
    val webClients = usersString
            .split(",")
            .map {username ->
                username to WebClient.create(vertx)
                .get(443, "https://porofessor.gg", "/live/euw/$username")
                .ssl(true)
                .putHeader("Accept", "application/json")
                .`as`(BodyCodec.string())
                .expect(ResponsePredicate.SC_OK)
            }

    //"https://porofessor.gg/live/euw/tinyangrykitten"
    // The summoner is not in-game, please retry later. The game must be on the loading screen or it must have started.
    fun start() {
        loopJob = GlobalScope.launch {
            while(true) {
                fetchUserGames()
                delay(pollingRate)
            }
        }
    }

    fun fetchUserGames() {
        webClients.forEach { it.second.send {
            asyncResult ->
                if (asyncResult.succeeded()) {
                    val isInGame = !asyncResult.result().body().contains("The summoner is not in-game, please retry later")

                    logger.info { "Checked user: ${it.first}, is ingame: $isInGame" }

                    if(isInGame) {
                        mqttClient.publish("game/league/${it.first}", Buffer.buffer("ingame"), MqttQoS.AT_MOST_ONCE,false, false )
                        logger.info {"user ${it.first} is ingame"}
                    } else logger.info {"user ${it.first} is not ingame"}

                }
            }
        }
    }
}