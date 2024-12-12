package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.Render3DEvent
import net.minecraft.client.model.Model
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(LivingEntityRenderer::class)
class MixinLivingEntityRenderer<T : LivingEntity, M : EntityModel<T>> {
    @Inject(
        method = ["render"],
        at = [At("HEAD")]
    )
    private fun renderHeadHook(
        entity : T,
        f : Float,
        g : Float,
        matrices : MatrixStack,
        consumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.EntityRenderer.Render.Pre(entity))
    }

    @Inject(
        method = ["render"],
        at = [At("TAIL")]
    )
    private fun renderTailHook(
        entity : T,
        f : Float,
        g : Float,
        matrices : MatrixStack,
        consumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.EntityRenderer.Render.Post(entity))
    }

    /*
    private var currentEntity : T? = null

    @Inject(
        method = ["render"],
        at = [At("HEAD")]
    )
    private fun render(
        entity : T,
        g : Float,
        f : Float,
        matrices : MatrixStack,
        consumers : VertexConsumerProvider,
        i : Int
    ) {
        currentEntity = entity
    }

    @Redirect(
        method = ["render"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
        )
    )
    private fun renderRedirectModelRenderHook(
        model : Model,
        matrices : MatrixStack,
        consumer : VertexConsumer,
        light : Int,
        overlay : Int,
        red : Float,
        green : Float,
        blue : Float,
        alpha : Float
    ) {
        if(!LavaHack.EVENT_BUS.post(Render3DEvent.EntityRenderer.RenderModel(currentEntity!!, model, matrices, consumer, light, overlay, red, green, blue, alpha)).cancelled) {
            model.render(matrices, consumer, light, overlay, red, green, blue, alpha)
        }
    }
     */
}