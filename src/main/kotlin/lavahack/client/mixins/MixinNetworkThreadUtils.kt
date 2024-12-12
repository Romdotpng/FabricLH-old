package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.PacketEvent
import net.minecraft.network.NetworkThreadUtils
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BundleS2CPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 14:48 of 31.05.2023
 */
@Suppress("FunctionName")
@Mixin(NetworkThreadUtils::class)
class MixinNetworkThreadUtils {
    private companion object {
        @JvmStatic
        @Inject(
            method = ["method_11072"],
            at = [At(
                value = "INVOKE",
                target = "Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V"
            )],
            cancellable = true
        )
        private fun method_11072InvokePacketApplyHook(
            listener : PacketListener,
            packet : Packet<*>,
            ci : CallbackInfo
        ) {
            LavaHack.EVENT_BUS.post(PacketEvent.Receive(packet, listener), ci)

            if(packet is BundleS2CPacket) {
                val bundlePackets = packet.packets

                for(bundlePacket in bundlePackets) {
                    LavaHack.EVENT_BUS.post(PacketEvent.Receive(bundlePacket, listener), ci)
                }
            }
        }
    }
}