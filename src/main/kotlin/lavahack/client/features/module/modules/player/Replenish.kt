package lavahack.client.features.module.modules.player

import lavahack.client.features.module.*
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Stopwatch
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.inventoryHotbarSwap

@Module.Info(
    name = "Replenish",
    description = "Automatically replenishes your hotbar",
    category = Module.Category.PLAYER
)
class Replenish : Module() {
    init {
        val threshold = register(SettingNumber("Threshold", 5, 0..63))
        val stackThreshold = register(SettingNumber("Stack Threshold", 5, 0..63))
        val delay = register(SettingNumber("Delay", 0, 0..200))

        val timer = Stopwatch()

        fun refillSlots(
            threshold : Int,
            stackThreshold : Int
        ) : Map<Int, Int> {
            val slots = mutableMapOf<Int, Int>()

            for (hotbarSlot in InventoryLocations.Hotbar.inventorySlots) {
                val hotbarStack = mc.player!!.inventory.getStack(hotbarSlot)
                val hotbarItem = hotbarStack.item

                if (!hotbarStack.isEmpty) {
                    for (inventorySlot in InventoryLocations.Inventory.inventorySlots) {
                        val inventoryStack = mc.player!!.inventory.getStack(inventorySlot)
                        val inventoryItem = inventoryStack.item

                        if (inventoryItem == hotbarItem && hotbarStack.count <= threshold && inventoryStack.count >= stackThreshold) {
                            slots[hotbarSlot] = inventorySlot

                            break
                        }
                    }
                }
            }

            return slots
        }

        tickListener {
            if (mc.world == null || mc.player == null || mc.currentScreen != null || !timer.passed(delay.value, true)) {
                return@tickListener
            }

            val slots = refillSlots(threshold.value, stackThreshold.value)

            for(pair in slots) {
                val to = pair.key
                val from = pair.value

                if(from != -1) {
                    inventoryHotbarSwap(from, to)
                }
            }
        }
    }
}