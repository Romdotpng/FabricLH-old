package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.moveListener
import lavahack.client.utils.client.interfaces.impl.receiveListener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.mc
import lavahack.client.utils.moving
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket

object GrimAnticheat : SubSystem(
    "Grim Anticheat"
) {
    private val UNFLAG_INTERACTS = register(Setting("Unflag Interacts", false))

    override fun init() {
        var flagged = false
        var ticks = 0

        receiveListener {
            val packet = it.packet

            when(packet) {
                is PlayerPositionLookS2CPacket -> {
                    if(UNFLAG_INTERACTS.value && !flagged) {
                        //TODO: wtf?
                        mc.options.sneakKey.isPressed = true
                        mc.player!!.isSneaking = true
                        mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player!!, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY))

                        flagged = true
                        ticks = 0
                    }
                }
            }
        }

        moveListener {
            if(UNFLAG_INTERACTS.value && moving()) {
                //TODO: setting for magic number
                if(flagged && ticks >= 3) {
                    mc.options.sneakKey.isPressed = false
                    mc.player!!.isSneaking = false
                    mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player!!, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY))

                    flagged = false
                }

                ticks++
            }
        }
    }
}