package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.Stopwatch
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.countItems
import lavahack.client.utils.mc
import net.minecraft.item.Items
import kotlin.math.max

object InventoryManager : SubSystem(
    "Inventory Manager"
) {
    internal var CRYSTALS_PER_SECOND = 1

    override fun init() {
        val stopwatch = Stopwatch()
        var prev = -1

        tickListener {
            if(mc.player == null || mc.world == null) {
                CRYSTALS_PER_SECOND = 0

                return@tickListener
            }

            if(stopwatch.passed(1000L, true)) {
                CRYSTALS_PER_SECOND = 0
            }


            val count = countItems(Items.END_CRYSTAL, InventoryLocations.Inventory) + countItems(Items.END_CRYSTAL, InventoryLocations.Hotbar) + countItems(Items.END_CRYSTAL, InventoryLocations.Offhand)

            CRYSTALS_PER_SECOND += max(prev - count, 0)
            prev = count
        }
    }
}

val CRYSTALS_PER_SECOND get() = InventoryManager.CRYSTALS_PER_SECOND