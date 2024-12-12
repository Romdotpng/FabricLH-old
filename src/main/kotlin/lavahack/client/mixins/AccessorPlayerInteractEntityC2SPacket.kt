package lavahack.client.mixins

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(PlayerInteractEntityC2SPacket::class)
interface AccessorPlayerInteractEntityC2SPacket {
    @get:Accessor("entityId")
    val id : Int
}