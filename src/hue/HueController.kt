package hue

import com.natpryce.konfig.Configuration
import inkapplications.shade.Shade
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.constructs.percent
import inkapplications.shade.lights.LightStateModification
import mu.KotlinLogging

private val logger = KotlinLogging.logger{}

class HueController : KoinComponent {
    val shade : Shade by inject()
    val config : Configuration by inject()
    val tokenStorage : TokenStorage by inject()

    fun findIdOfGroupName(name : String) = runBlocking {
        shade
        .groups
        .getGroups()
        .entries
        .find { it.value.name.equals(name,true ) }
        ?.key
    }

    fun getLightsInGroup(group : HueName) = getLightsInGroup(
            HueID(findIdOfGroupName(group.name) ?: "")
    )
    fun getLightsInGroup(group : HueID) = runBlocking {
        shade.groups.getGroup(group.id).lights
    }


    private fun modifyGroup(group : HueID, modification : LightStateModification) =
            runBlocking {
                getLightsInGroup(group)
                        ?.forEach { shade.lights.setState(it, modification) }
                        ?: logger.error { "Hue group not found: $group" }
            }

    private fun modifyGroup(group : HueName, modification : LightStateModification) =
            runBlocking {
                getLightsInGroup(group)
                        ?.forEach { shade.lights.setState(it, modification) }
                        ?: logger.error { "Hue group not found: $group" }
            }

    fun changeBrightnessOfGroup(group : HueName, brightness : Int) =
            modifyGroup(
                group,
                LightStateModification(brightness = brightness.percent)
            )

    fun changeColorOfGroup(group : HueName) { TODO() }

    fun toggleGroup(group : HueName, turnOn : Boolean) = modifyGroup(
            group,
            LightStateModification(on = turnOn)
    )

    fun getStateOfGroup(group : HueName) = runBlocking {
        getLightsInGroup(group)
                ?.map { shade.lights.getLight(it) }
                ?.map {GroupState.fromLightState(it.state)}
                ?.let {
                    list -> list.fold( list.first() ) {
                        acc, next -> acc + next
                    }
                } ?: GroupState()
    }

    fun init() =
        runBlocking {
            if (tokenStorage.getToken() == null) {
                logger.info { "Connecting to bridge, press the top bridge button" }
                shade.auth.awaitToken()
                logger.info { "Connected to bridge" }
            }
        }
}