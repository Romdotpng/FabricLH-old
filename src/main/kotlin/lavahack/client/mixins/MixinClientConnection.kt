package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.PacketEvent
import lavahack.client.utils.client.interfaces.mixins.IClientConnection
import net.minecraft.network.ClientConnection
import net.minecraft.network.PacketCallbacks
import net.minecraft.network.packet.Packet
import org.jetbrains.annotations.Nullable
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 1:17 of 29.05.2023
 */
@Mixin(ClientConnection::class)
class MixinClientConnection : IClientConnection {
    @Shadow
    fun isOpen() = false

    @Shadow
    fun sendQueuedPackets() { }

    @Shadow
    fun sendImmediately(
        packet : Packet<*>,
        callbacks : PacketCallbacks?
    ) { }

    @Inject(
        method = ["send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun sendHeadHook(
        packet : Packet<*>,
        callbacks : @Nullable PacketCallbacks,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(PacketEvent.Send(packet), ci)
    }

    override fun sendNoEvent(
        packet : Packet<*>
    ) {
        if(isOpen()) {
            sendQueuedPackets()
            sendImmediately(packet, null)
        }
    }
}