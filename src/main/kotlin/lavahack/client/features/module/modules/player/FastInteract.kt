package lavahack.client.features.module.modules.player

import lavahack.client.features.module.Module
import lavahack.client.mixins.AccessorMinecraftClient
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.item.BlockItem
import net.minecraft.item.Items

@Module.Info(
    name = "FastInteract",
    description = "Interacts faster.",
    category = Module.Category.PLAYER
)
class FastInteract : Module() {
    init {
        val exp = register(Setting("Exp", false))
        val blocks = register(Setting("Blocks", false))
        val others = register(Setting("Others", false))

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            if(when(mc.player!!.mainHandStack.item) {
                Items.EXPERIENCE_BOTTLE -> exp.value
                is BlockItem -> blocks.value
                else -> others.value
            }) {
                (mc as AccessorMinecraftClient).itemUseCooldown = 0
            }
        }
    }
}