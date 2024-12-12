package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.InputController
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Stopwatch
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.findInventoryItem
import lavahack.client.utils.inventorySwap
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.SwordItem

/**
 * @author _kisman_
 * @since 19:06 of 31.05.2023
 */
@Module.Info(
    name = "Offhand",
    description = "Controls offhand slot",
    aliases = "AutoTotem",
    category = Module.Category.COMBAT
)
class Offhand : Module() {
    init {
        val item = register(SettingEnum("Item", AvailableItems.Crystal))
        val health = register(SettingNumber("Health", 14, 0..36))
        val delay = register(SettingNumber("Delay", 0L, 0L..40L))
        val swordGapple = register(Setting("Sword Gapple", false, "Sword -> Gapple"))
        val rightClickGapple = register(Setting("Right Click Gapple", false, "^ Right click required"))

        val stopwatch = Stopwatch()

        tickListener {
            if(mc.player == null || mc.world == null || !stopwatch.passed(delay.value, true) || mc.currentScreen != null) {
                return@tickListener
            }

            val current = if(swordGapple.value && mc.player!!.mainHandStack.item is SwordItem && (!rightClickGapple.value || InputController.pressedButton(1))) {
                Items.GOLDEN_APPLE
            } else if((mc.player!!.health + mc.player!!.absorptionAmount) >= health.value) {
                item.valEnum.item
            } else {
                Items.TOTEM_OF_UNDYING
            }

            if(mc.player!!.offHandStack.item != current) {
                val slot = findInventoryItem(current)

                inventorySwap(slot, 45)
            }
        }
    }

    enum class AvailableItems(
        val item : Item
    ) {
        Totem(Items.TOTEM_OF_UNDYING),
        Crystal(Items.END_CRYSTAL),
        Gapple(Items.GOLDEN_APPLE)
    }
}