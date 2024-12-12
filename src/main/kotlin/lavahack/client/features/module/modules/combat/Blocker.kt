package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.impl.*
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

@Module.Info(
    name = "Blocker",
    description = "Attempts to extend your surround when it's being broken.",
    category = Module.Category.COMBAT,
    beta = true
)
class Blocker : Module() {
    init {
        val surround = register(Setting("Surround", true))
        val cev = register(Setting("Cev", true))
        val placer = register(BlockPlacerPattern(this, this, useCrystalBreaker = true))

        val toPlace = mutableListOf<BlockPos>()

        fun reset() {
            toPlace.clear()
        }

        enableCallback {
            reset()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                reset()

                return@tickListener
            }

            val posses = mutableSetOf<BlockPos>()
            val feetBlocks = feetBlocks(mc.player!!)

            for(center in toPlace) {
                for(direction in Direction.values()) {
                    if(direction != Direction.DOWN) {
                        val pos = center.offset(direction)

                        if(placeable(pos) && !feetBlocks.contains(pos)) {
                            posses.add(pos)
                        }
                    }
                }
            }

            placer.place(posses, Items.OBSIDIAN)
        }

        receiveListener {
            when(it.packet) {
                is BlockBreakingProgressS2CPacket -> {
                    val pos = it.packet.pos
                    val block = pos.block()

                    if(pos.reachable && block.breakable) {
                        val dynamicBlocks = dynamicBlocks(mc.player!!)
                        val aboveHeadBlocks = feetBlocks(mc.player!!, 2)

                        if((surround.value && dynamicBlocks.contains(pos)) || (cev.value && aboveHeadBlocks.contains(pos))) {
                            toPlace.add(pos)
                        }
                    }
                }
            }
        }
    }
}