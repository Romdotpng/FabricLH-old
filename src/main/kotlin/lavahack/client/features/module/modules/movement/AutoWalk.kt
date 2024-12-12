package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.utils.client.interfaces.impl.tickListener

@Module.Info(
    name = "AutoWalk",
    description = "Automatically walks you forward",
    category = Module.Category.MOVEMENT
)
class AutoWalk : Module() {
    init {
        disableCallback {
            mc.options?.forwardKey?.isPressed = false
        }

        tickListener {
            mc.options?.forwardKey?.isPressed = true
        }
    }
}