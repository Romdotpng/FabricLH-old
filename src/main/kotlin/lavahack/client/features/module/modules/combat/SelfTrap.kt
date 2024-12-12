package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.client.ranges.step
import lavahack.client.utils.distanceSq
import lavahack.client.utils.dynamicBlocks
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos

@Module.Info(
    name = "SelfTrap",
    description = "Traps you",
    category = Module.Category.COMBAT,
    beta = true
)
class SelfTrap : Module() {
    init {
        val head = register(Setting("Head", false))
        val cev = register(Setting("Cev", true))
        val placer = register(BlockPlacerPattern(this, this))

        val enemyTriggerGroup = register(SettingGroup("Enemy Trigger"))
        val enemyTriggerState = register(enemyTriggerGroup.add(Setting("State", false)))
        val enemyTriggerDistance = register(enemyTriggerGroup.add(SettingNumber("Distance", 2.0, 1.0..5.0 step 0.5)))

        tickListener {
            if(mc.player == null || mc.world == null || (enemy != null && enemyTriggerState.value && mc.player!!.pos distanceSq enemy!!.pos > enemyTriggerDistance.value * enemyTriggerDistance.value)) {
                return@tickListener
            }

            val posses = mutableListOf<BlockPos>()

            if(head.value) {
                val dynamicBlocks = dynamicBlocks(mc.player!!, 1)

                posses.addAll(dynamicBlocks)
            }

            if(cev.value) {
                val pos = mc.player!!.blockPos.up(2)

                posses.add(pos)
            }

            placer.place(posses, Items.OBSIDIAN)
        }
    }
}