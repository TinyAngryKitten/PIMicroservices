package actions

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

sealed class IntervalAction {
    @ExperimentalTime
    abstract val interval : Duration
    abstract fun performAction()
}