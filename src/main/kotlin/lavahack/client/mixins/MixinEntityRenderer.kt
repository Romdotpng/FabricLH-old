package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.Render3DEvent
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 18:00 of 02.06.2023
 */
@Mixin(EntityRenderer::class)
class MixinEntityRenderer<T : Entity> {
    @Inject(
        method = ["renderLabelIfPresent"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderLabelIfPresentHeadHook(
        entity : T,
        text : Text,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.EntityNametag(entity), ci)
    }
}