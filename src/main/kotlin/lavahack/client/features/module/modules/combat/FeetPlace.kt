package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.features.module.threadCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.*
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.enums.Swaps
import lavahack.client.utils.client.interfaces.impl.*
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author _kisman_
 * @since 0:09 of 09.07.2023
 */
@Module.Info(
    name = "FeetPlace",
    description = "Surrounds player's feet with obby to decrease crystal damage",
    aliases = "Surround",
    category = Module.Category.COMBAT
)
class FeetPlace : Module() {
    init {
        val placed = mutableSetOf<BlockPos>()

        val pattern = register(BlockPlacerPattern(this, null, useCrystalBreaker = true) { placed.add(it) })
        val centring = register(Setting("Centring", false))
        val dynamic = register(Setting("Dynamic", true))
        val feetBlocks = register(Setting("Feet Blocks", true))
        val toggleAfter = register(SettingEnum("Toggle After", ToggleAfter.Moved))
        val fastReplace = register(Setting("Fast Replace", false))
        val detectPlacedCrystal = register(Setting("Detect Placed Crystal", false))
        val detectBrokenCrystal = register(Setting("Detect Broken Crystal", false))

        val posses = ConcurrentLinkedQueue<BlockPos>()

        fun handlePlace() {
            pattern.place(posses, Items.OBSIDIAN)
        }

        enableCallback {
            if(mc.player == null || mc.world == null) {
                state = false

                return@enableCallback
            }

            pattern.reset()
            placed.clear()
            posses.clear()

            if(feetBlocks.value) {
                val blocks = feetBlocks(mc.player!!, -1).stream().filter { it.block() == Blocks.AIR }.toList()

                posses.addAll(blocks)
            }

            if(dynamic.value) {
                val blocks = dynamicBlocks(mc.player!!).stream().filter { it.block() == Blocks.AIR }.toList()

                posses.addAll(blocks)
            }
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                state = false

                return@tickListener
            }

            state = !when(toggleAfter.valEnum) {
                ToggleAfter.Surrounded -> posses.isEmpty() && placed.isNotEmpty()
                ToggleAfter.Moved -> mc.player!!.prevY != mc.player!!.y
                ToggleAfter.Never -> false
            }
        }

        threadCallback {
            handlePlace()
        }

        breakListener {
            if(fastReplace.value) {
                val pos = it.pos

                if (posses.contains(pos)) {
                    pattern.place(pos, Items.OBSIDIAN)
                }
            }
        }

        entityAddListener {
            if(detectPlacedCrystal.value) {
                handlePlace()
            }
        }

        entityRemoveListener {
            if(detectBrokenCrystal.value) {
                handlePlace()
            }
        }
    }

    enum class ToggleAfter {
        Surrounded,
        Moved,
        Never
    }
}