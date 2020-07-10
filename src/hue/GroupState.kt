package hue

import inkapplications.shade.lights.LightState

data class GroupState(
        val groupExists : Boolean = false,
        val brightness : GroupBrightness = GroupBrightness.VaryingBrightness,
        val color : GroupColor = GroupColor.VaryingColor,
        val onState : GroupOnState = GroupOnState.VaryingOnState
) {

    fun combine(state : GroupState) =
            GroupState(
                    true,
                    brightness + state.brightness,
                    color + state.color,
                    onState + state.onState
            )

    operator fun plus(other : GroupState) = combine(other)

    companion object {
        fun fromLightState(state : LightState) =
                GroupState(
                        true,
                        GroupBrightness.CommonBrightness( (state.brightness.fractionalValue*100).toInt() ),
                        GroupColor.CommonColor(),
                        GroupOnState.CommonOnState(state.on)
                )
    }
}

sealed class GroupBrightness {
    class CommonBrightness(val brightness: Int) : GroupBrightness()
    object VaryingBrightness : GroupBrightness()

    operator fun plus(other : GroupBrightness) : GroupBrightness {
        if(this is CommonBrightness && other is CommonBrightness) {
            if(this.brightness == other.brightness) return other
        }
        return VaryingBrightness
    }
}

sealed class GroupColor {
    class CommonColor : GroupColor()
    object VaryingColor : GroupColor()

    operator fun plus(other : GroupColor) : GroupColor {
        if(this is CommonColor && other is CommonColor) {
            return other
        }
        return VaryingColor
    }
}

sealed class GroupOnState {
    class CommonOnState(val isOn : Boolean) : GroupOnState()
    object VaryingOnState : GroupOnState()

    operator fun plus(other : GroupOnState) : GroupOnState {
        if(this is CommonOnState && other is CommonOnState) {
            if(this.isOn == other.isOn) return other
        }
        return VaryingOnState
    }
}