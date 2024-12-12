package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.utils.armorSlots
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.findInventoryItem
import lavahack.client.utils.inventorySwap
import net.minecraft.item.ArmorItem

/**
 * @author _kisman_
 * @since 10:09 of 17.07.2023
 */
@Module.Info(
    name = "AutoArmor",
    description = "Automatically equips armor from your inventory.",
    category = Module.Category.COMBAT
)
class AutoArmor : Module() {
    init {
        tickListener {
            if(mc.player == null || mc.world == null || mc.currentScreen != null) {
                return@tickListener
            }

            val types = mutableListOf<ArmorItem.Type>()

            for(stack in mc.player!!.inventory.armor) {
                if(!stack.isEmpty) {
                    val item = stack.item

                    if(item is ArmorItem) {
                        val type = item.type

                        types.add(type)
                    }
                }
            }

            for(type in ArmorItem.Type.values()) {
                if(!types.contains(type)) {
                    val from = findInventoryItem {
                        it is ArmorItem && it.type == type
                    }

                    val to = armorSlots[type]!!

                    if(from != -1) {
                        inventorySwap(from, to)
                    }
                }
            }
        }
    }
}