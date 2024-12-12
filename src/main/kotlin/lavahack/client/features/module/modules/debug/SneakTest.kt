package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.features.module.enableCallback

@Module.Info(
    name = "SneakTest",
    category = Module.Category.DEBUG
)
class SneakTest : Module() {
    init {
        enableCallback {
            mc.options.sneakKey.isPressed = true
        }

        disableCallback {
            mc.options.sneakKey.isPressed = false
        }
    }
}