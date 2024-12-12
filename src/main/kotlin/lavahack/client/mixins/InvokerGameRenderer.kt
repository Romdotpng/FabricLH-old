package lavahack.client.mixins

import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(GameRenderer::class)
interface InvokerGameRenderer {
    @Invoker("renderHand")
    fun renderHand0(
        matrices : MatrixStack,
        camera : Camera,
        tickDelta : Float
    )

    @Invoker("loadPostProcessor")
    fun loadPostProcessor0(
        identifier : Identifier
    )
}