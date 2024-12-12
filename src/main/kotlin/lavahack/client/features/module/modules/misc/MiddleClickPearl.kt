package lavahack.client.features.module.modules.misc

import lavahack.client.event.events.InputEvent
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.enums.KeyActions
import lavahack.client.utils.client.enums.Swaps
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.register
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.util.Hand

@Module.Info(
    name = "MiddleClickPearl",
    description = "Throws pearl after pressing middle mouse button",
    category = Module.Category.MISC
)
class MiddleClickPearl : Module() {
    init {
        val swapper = register(SettingEnum("Swap", Swaps.Silent))
        val onlyMiddleButton = register(Setting("Only Middle Button", true))

        listener<InputEvent.Mouse> {
            if(mc.player == null || mc.world == null || it.action == KeyActions.Repeat || it.button == 0 || it.button == 1 || (it.button != 2 && onlyMiddleButton.value)) {
                return@listener
            }

            val slot = InventoryLocations.Hotbar.findInventoryItem(Items.ENDER_PEARL)

            if(slot != -1) {
                swapper.valEnum.pre(slot)

                mc.player!!.networkHandler.sendPacket(PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 1))

                swapper.valEnum.post()
            }
        }
    }
}