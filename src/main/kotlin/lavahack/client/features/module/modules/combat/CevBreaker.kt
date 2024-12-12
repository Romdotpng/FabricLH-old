package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.features.module.modules.exploit.PacketMine
import lavahack.client.features.module.threadCallback
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.utils.block
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.leftClickEntity
import net.minecraft.block.Blocks
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.item.Items

@Module.Info(
    name = "CevBreaker",
    category = Module.Category.COMBAT
)
class CevBreaker : Module() {
    init {
        val placer = register(BlockPlacerPattern(this, this))

        var placed = false
        var broken = false

        fun reset() {
            placed = false
            broken = false
        }

        enableCallback {
            reset()
        }

        tickListener {

        }

        threadCallback {
            if(enemy == null) {
                reset()

                return@threadCallback
            }

            val data = PacketMine.data
            val pos = data?.pos
            val block = pos?.block()

            if(block == Blocks.AIR) {
                if(broken) {
                    if(!placed) {
                        placer.place(pos!!, Items.OBSIDIAN) {
                            placed = true
                        }
                    }
                } else {
                    val entities = mc.world!!.entities

                    for(entity in entities) {
                        if(entity is EndCrystalEntity) {
                            val crystalPos = entity.blockPos
                            val crystalBasePos = crystalPos.down()

                            println("crystal base $crystalBasePos")

                            if(pos == crystalBasePos) {
                                leftClickEntity(entity, swing = true)

                                placed = false
                                broken = true
                            }
                        }
                    }
                }
            } else {
                reset()
            }
        }
    }
}