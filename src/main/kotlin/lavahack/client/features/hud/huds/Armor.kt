package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.utils.durability
import lavahack.client.utils.durabilityColor
import lavahack.client.utils.mc
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import kotlin.math.roundToInt

private val SLOTS = mapOf(
    0 to EquipmentSlot.HEAD,
    1 to EquipmentSlot.CHEST,
    2 to EquipmentSlot.LEGS,
    3 to EquipmentSlot.FEET
)

//TODO: optimize using mc.player!!.getEquippedStack(SLOTS[it]!!).item
@Module.Info(
    name = "Armor",
    description = "Shows play armor info"
)
class Armor : Hud.ItemList(
    { mc.player!!.getEquippedStack(SLOTS[it]!!).item is ArmorItem },
    {
        val stack = mc.player!!.getEquippedStack(SLOTS[it]!!)
        val item = stack.item

        item to if(stack.isDamageable) "${(stack.durability * 100.0).roundToInt()}%" else ""
    },
    { mc.player!!.getEquippedStack(SLOTS[it]!!).durabilityColor }
)