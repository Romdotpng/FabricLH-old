package lavahack.client.mixins

import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.network.SequencedPacketCreator
import net.minecraft.client.world.ClientWorld
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(ClientPlayerInteractionManager::class)
interface InvokerClientPlayerInteractionManager {
    @Invoker("sendSequencedPacket")
    fun sendSequencedPacket0(
        world : ClientWorld,
        creator : SequencedPacketCreator
    )
}