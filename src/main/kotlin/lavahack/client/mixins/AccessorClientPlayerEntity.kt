package lavahack.client.mixins

import net.minecraft.client.network.ClientPlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(ClientPlayerEntity::class)
interface AccessorClientPlayerEntity {
    @get:Accessor("usingItem")
    @set:Accessor("usingItem")
    var usingItem : Boolean
}