package lavahack.client.features.module.modules.player

import lavahack.client.features.module.Module
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.item.Items

@Module.Info(
    name = "AutoEat",
    description = "Automatically eats food",
    category = Module.Category.PLAYER
)
class AutoEat : Module() {
    init {
        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            val stack = mc.player!!.mainHandStack
            val item = stack.item
            val component = item.foodComponent

            if(component != null) {
                mc.options.useKey.isPressed = true
            }
        }
    }
}