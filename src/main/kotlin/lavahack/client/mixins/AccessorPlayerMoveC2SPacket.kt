package lavahack.client.mixins

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Mutable
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 4:30 of 29.07.2023
 */
@Mixin(PlayerMoveC2SPacket::class)
interface AccessorPlayerMoveC2SPacket {
    @get:Accessor("yaw")
    @set:Accessor("yaw")
    @set:Mutable
    var yaw : Float

    @get:Accessor("pitch")
    @set:Accessor("pitch")
    @set:Mutable
    var pitch : Float
}