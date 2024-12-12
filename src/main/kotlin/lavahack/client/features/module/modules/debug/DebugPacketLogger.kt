package lavahack.client.features.module.modules.debug

import lavahack.client.event.events.PlayerEvent
import lavahack.client.features.module.Module
import lavahack.client.utils.client.interfaces.impl.sendListener
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

@Module.Info(
    name = "DebugPacketLogger",
    category = Module.Category.DEBUG
)
class DebugPacketLogger : Module() {
    init {
        sendListener {
            when(
                val packet = it.packet
            ) {
                is PlayerMoveC2SPacket -> {
                    if(packet.changesLook()) {
                        val yaw = packet.getYaw(0f)
                        val pitch = packet.getPitch(0f)
                        val spoofed = PlayerEvent.Motion.IS_IN

                        println("PlayerMoveC2SPacket.Look >> $yaw $pitch${if(spoofed) " (spoofed)" else ""}")
                    }
                }

                is PlayerInteractItemC2SPacket -> {
                    val hand = packet.hand
                    val sequence = packet.sequence

                    println("PlayerInteractItemC2SPacket >> $hand $sequence")
                }
            }
        }
    }
}