package lavahack.client.mixins

import net.minecraft.client.network.ClientPlayerInteractionManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(ClientPlayerInteractionManager::class)
interface AccessorClientPlayerInteractionManager {
    @get:Accessor("breakingBlock")
    @set:Accessor("breakingBlock")
    var breakingBlock : Boolean
}