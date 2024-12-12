package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnumRegistry
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.IRegistry
import lavahack.client.utils.client.interfaces.impl.Registry
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.Hand

/**
 * @author _kisman_
 * @since 23:17 of 14.07.2023
 */
@Module.Info(
    name = "Phase",
    description = "Allows you to move through a wall.",
    category = Module.Category.MOVEMENT
)
object Phase : Module() {
    init {
        val mode = register(SettingEnumRegistry("Mode", Modes.Pearl))

        displayInfo = { mode.valEnum.toString() }
    }

    enum class Modes(
        override val registry : Registry
    ) : IRegistry {
        Pearl(object : Registry() {
            init {
                val autoDisable = register(Setting("Auto Disable", true))
                val cooldownBypass = register(Setting("Cooldown Bypass", false))

                tickListener {
                    if(mc.player == null || mc.world == null) {
                        return@tickListener
                    }

                    val slot = InventoryLocations.Hotbar.findInventoryItem(Items.ENDER_PEARL)
                    val prevSlot = mc.player!!.inventory.selectedSlot
                    val prevPitch = mc.player!!.pitch

                    if(mc.player!!.horizontalCollision && slot != -1) {
                        if(cooldownBypass.value) {
                            //TODO: cooldown bypass
                        }

                        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(mc.player!!.yaw, 84f, mc.player!!.isOnGround))
                        mc.player!!.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(slot))
                        mc.player!!.networkHandler.sendPacket(PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0))
                        mc.player!!.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(prevSlot))
                        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(mc.player!!.yaw, prevPitch, mc.player!!.isOnGround))

                        if(autoDisable.value) {
                            Phase.toggle(false)
                        }
                    }
                }
            }
        })
    }
}