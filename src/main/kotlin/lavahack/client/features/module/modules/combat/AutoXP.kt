package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Stopwatch
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.enums.Swaps
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.Hand

@Module.Info(
    name = "AutoXP",
    description = "Automatically throws experience bottles",
    category = Module.Category.COMBAT
)
class AutoXP : Module() {
    init {
        val swap = register(SettingEnum("Swap", Swaps.Packet))
        val delay = register(SettingNumber("Delay", 1, 0..20))
        val feet = register(Setting("Feet", true))
        val swing = register(Setting("Swing", true))

        val timer = Stopwatch()

        tickListener {
            if (mc.player == null || mc.world == null || !timer.passed(delay.value * 50, true)) {
                return@tickListener
            }

            val slot = InventoryLocations.Hotbar.findInventoryItem(Items.EXPERIENCE_BOTTLE)
            val pitch = mc.player!!.pitch
            val stack = mc.player!!.mainHandStack
            val item = stack.item
            val mainhand = item == Items.EXPERIENCE_BOTTLE

            if (slot != -1) {
                if(feet.value) {
                    mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(mc.player!!.yaw, 90F, mc.player!!.isOnGround))
                }

                if(!mainhand) {
                    swap.valEnum.pre(slot)
                }

                mc.interactionManager!!.interactItem(mc.player, Hand.MAIN_HAND)

                if(swing.value) {
                    mc.player!!.networkHandler.sendPacket(HandSwingC2SPacket(Hand.MAIN_HAND))
                }

                if(!mainhand) {
                    swap.valEnum.post()
                }

                if(feet.value) {
                    mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(mc.player!!.yaw, pitch, mc.player!!.isOnGround))
                }
            }
        }
    }
}