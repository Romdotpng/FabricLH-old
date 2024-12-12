package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.utils.client.interfaces.impl.tickListener

/**
 * @author _kisman_
 * @since 10:25 of 11.05.2023
 */
@Module.Info(
    name = "Sprint",
    description = "Makes you faster",
    category = Module.Category.MOVEMENT
)
class Sprint : Module() {
    init {
        disableCallback {
            if(mc.player == null || mc.world == null) {
                return@disableCallback
            }

            mc.player!!.isSprinting = false
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            mc.player!!.isSprinting = true
        }
    }
}