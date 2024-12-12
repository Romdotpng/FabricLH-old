package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem

object TimerManager : SubSystem(
    "Timer Manager"
) {
    var multiplier = 1f
}

var timer : Number
    get() = TimerManager.multiplier
    set(value) {
        TimerManager.multiplier = value.toFloat()
    }