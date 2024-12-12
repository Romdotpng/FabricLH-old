package lavahack.client.mixins

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(WorldRenderer::class)
interface InvokerWorldRenderer {
    @Invoker("renderEntity")
    fun renderEntity0(
        entity : Entity,
        cameraX : Double,
        cameraY : Double,
        cameraZ : Double,
        delta : Float,
        matrices : MatrixStack,
        consumers : VertexConsumerProvider
    )
}