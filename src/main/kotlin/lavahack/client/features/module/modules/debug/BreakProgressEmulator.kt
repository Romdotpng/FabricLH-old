package lavahack.client.features.module.modules.debug

import lavahack.client.LavaHack
import lavahack.client.event.events.PacketEvent
import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket
import net.minecraft.util.hit.BlockHitResult

@Module.Info(
    name = "BreakProgressEmulator",
    description = "Emulates BlockBreakingProgressS2CPacket",
    category = Module.Category.DEBUG
)
class BreakProgressEmulator : Module() {
    init {
        enableCallback {
            if(mc.player == null || mc.world == null) {
                return@enableCallback
            }

            val result = mc.crosshairTarget

            if(result is BlockHitResult) {
                val pos = result.blockPos
                val packet = BlockBreakingProgressS2CPacket(-1, pos, 0)

                LavaHack.EVENT_BUS.post(PacketEvent.Receive(packet, mc.player!!.networkHandler))

                state = false
            }
        }
    }
}