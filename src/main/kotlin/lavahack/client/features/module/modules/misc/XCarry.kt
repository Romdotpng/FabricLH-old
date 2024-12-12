package lavahack.client.features.module.modules.misc

import lavahack.client.features.module.Module
import lavahack.client.utils.client.interfaces.impl.sendListener
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket

@Module.Info(
    name = "XCarry",
    category = Module.Category.MISC
)
class XCarry : Module() {
    init {
        sendListener {
            val packet = it.packet

            when(packet) {
                is CloseHandledScreenC2SPacket -> {
                    val id = packet.syncId

                    if(id == mc.player!!.currentScreenHandler?.syncId) {
                        it.cancel()
                    }
                }
            }
        }
    }
}