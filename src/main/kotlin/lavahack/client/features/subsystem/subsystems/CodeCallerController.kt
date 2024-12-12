package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem

object CodeCallerController : SubSystem(
    "Code Caller Controller"
) {
    object WorldRenderer {
        var FROM_RENDER = false
    }
}