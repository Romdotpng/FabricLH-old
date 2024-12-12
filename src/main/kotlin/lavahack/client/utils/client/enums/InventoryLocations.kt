package lavahack.client.utils.client.enums

import lavahack.client.utils.mc
import lavahack.client.utils.state
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 19:16 of 31.05.2023
 */
enum class InventoryLocations(
    private val containerSlots : IntRange,
    val inventorySlots : IntRange
) {
    Armor(5..8, 36..39),
    Inventory(9..35, 9..35),
    Hotbar(36..44, 0..8),
    Offhand(45..45, 40..40)

    ;

    fun findContainerItem(
        item : Item
    ) = findContainerItem { it == item }

    fun findInventoryItem(
        item : Item
    ) = findInventoryItem { it == item }

    fun findContainerItem(
        check : (Item) -> Boolean
    ) : Int {
        for((index, slot) in inventorySlots.toList().withIndex()) {
            val stack = mc.player!!.inventory.getStack(slot)!!
            val item = stack.item!!

            if(check(item)) {
                return containerSlots.toList()[index]
            }
        }

        return -1
    }

    fun findInventoryItem(
        check : (Item) -> Boolean
    ) : Int {
        for(slot in inventorySlots) {
            val stack = mc.player!!.inventory.getStack(slot)!!
            val item = stack.item!!

            if(check(item)) {
                return slot
            }
        }

        return -1
    }

    fun findBestInventoryTool(
        pos : BlockPos
    ) : Int {
        val state = pos.state()
        var bestSlot = -1
        var bestMultiplier = -1f

        for(slot in inventorySlots) {
            val stack = mc.player!!.inventory.getStack(slot)!!

            if(!stack.isEmpty) {
                var multiplier = stack.getMiningSpeedMultiplier(state)
                val efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack)

                if(efficiency > 1) {
                    multiplier += efficiency * efficiency + 1
                }

                if(multiplier > bestMultiplier) {
                    bestSlot = slot
                    bestMultiplier = multiplier
                }
            }
        }

        return bestSlot
    }
}