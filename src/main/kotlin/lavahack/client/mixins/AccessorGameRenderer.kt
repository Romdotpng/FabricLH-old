package lavahack.client.mixins

import net.minecraft.client.render.GameRenderer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(GameRenderer::class)
interface AccessorGameRenderer {
    @get:Accessor("renderHand")
    val renderHand : Boolean
}