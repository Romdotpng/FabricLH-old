package lavahack.client.features.module.modules.misc

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.placeable
import lavahack.client.utils.state
import net.minecraft.entity.Entity
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos

@Module.Info(
    name = "AntiFlee",
    description = "inzane",
    category = Module.Category.MISC
)
class AntiFlee : Module() {
    init {
        val placer = register(BlockPlacerPattern(this, null))

        fun getBlockList(
            entity : Entity,
            yOff : Int
        ) : MutableList<BlockPos> {
            val list = mutableListOf<BlockPos>()

            if (entity.velocity.x == 0.0 && entity.velocity.z == 0.0) {
                return list
            }

            val xAdd = entity.velocity.x * 3 - 1
            val zAdd = entity.velocity.z * 3 - 1

            val y = entity.pos.y.toInt() + yOff
            val box = entity.boundingBox.offset(xAdd, yOff.toDouble(), zAdd)
            val off = (box.maxY - box.minY) / 2

            val offsets = listOf(y, (y + off + yOff).toInt(), box.maxY.toInt())

            for(offset in offsets) {
                list.add(BlockPos(box.minX.toInt(), y, box.minZ.toInt()))
                list.add(BlockPos(box.minX.toInt(), y, box.maxZ.toInt()))
                list.add(BlockPos(box.maxX.toInt(), y, box.minZ.toInt()))
                list.add(BlockPos(box.maxX.toInt(), y, box.maxZ.toInt()))
            }

            return list.stream()
                .distinct()
                .filter { it.state().isReplaceable }
                .filter { placeable(it!!) }
                .toList()
                .toMutableList()
        }

        fun reset() {
            placer.reset()
        }

        enableCallback {
            reset()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                reset()

                return@tickListener
            }

            var blocks = getBlockList(mc.player!!, 0)

            //TODO: cleanup
            blocks.addAll(getBlockList(mc.player!!, 1))
            blocks = blocks.stream().distinct().toList()

            placer.place(blocks, Items.OBSIDIAN)
        }

    }
}