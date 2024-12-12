package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.client.ranges.step

/**
 * @author _kisman_
 * @since 10:26 of 11.05.2023
 */
@Module.Info(
    name = "Step",
    description = "Makes step height upper",
    aliases = "Spider",
    category = Module.Category.MOVEMENT
)
object Step : Module() {
    init {
        val height = register(SettingNumber("Height", 1f, 0.5f..4f step 0.5f))

        disableCallback {
            if(mc.player == null || mc.world == null) {
                return@disableCallback
            }

            mc.player!!.stepHeight = 0.5f
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            mc.player!!.stepHeight = height.value
        }
    }
}