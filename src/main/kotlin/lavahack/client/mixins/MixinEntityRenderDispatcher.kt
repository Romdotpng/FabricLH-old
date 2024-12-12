package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.Render3DEvent
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 19:46 of 28.05.2023
 */
@Suppress("UNUSED_PARAMETER")
@Mixin(EntityRenderDispatcher::class)
class MixinEntityRenderDispatcher {
    @Inject(
        method = ["render"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun <E : Entity> renderHeadHook(
        entity : E,
        x : Double,
        y : Double,
        z : Double,
        yaw : Float,
        tickDelta : Float,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render3DEvent.RenderEntity.Pre(entity, matrices, tickDelta, x, y, z, yaw, vertexConsumers)).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["render"],
        at = [At("TAIL")],
        cancellable = true
    )
    private fun <E : Entity> renderTailHook(
        entity : E,
        x : Double,
        y : Double,
        z : Double,
        yaw : Float,
        tickDelta : Float,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.RenderEntity.Post(entity, matrices, tickDelta, x, y, z, yaw, vertexConsumers))
    }

    @Inject(
        method = ["renderFire"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderWeatherHeadHook(
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        entity : Entity,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render3DEvent.EntityFire()).cancelled) {
            ci.cancel()
        }
    }
}