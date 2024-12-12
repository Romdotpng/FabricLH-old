package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.Targetable
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.dynamicBlocks
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos

@Module.Info(
    name = "AutoTrap",
    description = "Automatically traps nearest enemy",
    category = Module.Category.COMBAT,
    targetable = Targetable(
        nearest = true
    )
)
class AutoTrap : Module() {
    init {
        val head = register(Setting("Head", true))
        val feet = register(Setting("Feet", true))
        val cev = register(Setting("Cev", true))
        val placer = register(BlockPlacerPattern(this, null))

        tickListener {
            if(mc.player == null || mc.world == null || enemy == null) {
                return@tickListener
            }

            val posses = mutableSetOf<BlockPos>()

            if(feet.value) {
                val dynamicBlocks = dynamicBlocks(enemy!!)

                posses.addAll(dynamicBlocks)
            }

            if(head.value) {
                val dynamicBlocks = dynamicBlocks(enemy!!, 1)

                posses.addAll(dynamicBlocks)
            }

            if(cev.value) {
                val pos = enemy!!.blockPos.up(2)

                posses.add(pos)
            }

            placer.place(posses, Items.OBSIDIAN)
        }
    }
}