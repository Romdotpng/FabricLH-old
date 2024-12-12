package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.module.modules.render.ShadersModule
import lavahack.client.features.subsystem.subsystems.CodeCallerController
import lavahack.client.features.subsystem.subsystems.PostprocessShaderRenderer
import lavahack.client.utils.render.shader.PostProcessShader
import net.minecraft.block.BlockState
import net.minecraft.client.gl.PostEffectProcessor
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import org.joml.Matrix4f
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 11:23 of 26.05.2023
 */
@Mixin(WorldRenderer::class)
class MixinWorldRenderer {
    @Shadow
    private fun renderEntity(
        entity : Entity,
        cameraX : Double,
        cameraY : Double,
        cameraZ : Double,
        delta : Float,
        matrices : MatrixStack,
        consumers : VertexConsumerProvider
    ) { }

    @Inject(
        method = ["drawBlockOutline"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun drawBlockOutlineHeadHook(
        matrices : MatrixStack,
        vertexConsumer : VertexConsumer,
        entity : Entity,
        cameraX : Double,
        cameraY : Double,
        cameraZ : Double,
        pos : BlockPos,
        state : BlockState,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render3DEvent.DefaultBlockOutline()).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["renderWeather"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderWeatherHeadHook(
        manager : LightmapTextureManager,
        tickDelta : Float,
        cameraX : Double,
        cameraY : Double,
        cameraZ : Double,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render3DEvent.Weather()).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["renderWorldBorder"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderWorldBorderHeadHook(
        camera : Camera,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render3DEvent.WorldBorder()).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["renderEntity"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderEntityHeadHook(
        entity : Entity,
        cameraX : Double,
        cameraY : Double,
        cameraZ : Double,
        tickDelta : Float,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.WorldRenderer.RenderEntity.Pre(entity, matrices, vertexConsumers, cameraX, cameraY, cameraZ, tickDelta), ci)
    }

    @Inject(
        method = ["renderEntity"],
        at = [At("TAIL")]
    )
    private fun renderEntityTailHook(
        entity : Entity,
        cameraX : Double,
        cameraY : Double,
        cameraZ : Double,
        tickDelta : Float,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.WorldRenderer.RenderEntity.Post(entity))
    }

    @Inject(
        method = ["render"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderHeadHook(
        matrices : MatrixStack,
        tickDelta : Float,
        limitTime : Long,
        renderBlockOutline : Boolean,
        camera : Camera,
        gameRenderer : GameRenderer,
        lightmapTextureManager : LightmapTextureManager,
        positionMatrix : Matrix4f,
        ci : CallbackInfo
    ) {
        CodeCallerController.WorldRenderer.FROM_RENDER = true

        LavaHack.EVENT_BUS.post(Render3DEvent.WorldRenderer.Render.Start(matrices), ci)
    }

    @Inject(
        method = ["render"],
        at = [At("TAIL")]
    )
    private fun renderTailHook(
        matrices : MatrixStack,
        tickDelta : Float,
        limitTime : Long,
        renderBlockOutline : Boolean,
        camera : Camera,
        gameRenderer : GameRenderer,
        lightmapTextureManager : LightmapTextureManager,
        positionMatrix : Matrix4f,
        ci : CallbackInfo
    ) {
        CodeCallerController.WorldRenderer.FROM_RENDER = false

        LavaHack.EVENT_BUS.post(Render3DEvent.WorldRenderer.Render.End())
    }

    @Inject(
        method = ["checkEmpty"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun checkEmpty(
        matrices : MatrixStack,
        ci : CallbackInfo
    ) {
        ci.cancel()
    }

    @Redirect(
        method = ["render"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V",
            ordinal = 0
        )
    )
    private fun renderRedirectRenderInvokeHook(
        instance : PostEffectProcessor,
        delta : Float
    ) {
        val shader = PostprocessShaderRenderer.CURRENT_SHADER.valEnum.shader

        if(shader is PostProcessShader && ShadersModule.state && (ShadersModule.PLAYERS.value)) {
            shader.renderEffect()
        } else {
            instance.render(delta)
        }
    }

    /*@Redirect(
        method = ["render"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"
        )
    )
    private fun renderRedirectRenderEntityInvokeHook(
        instance : WorldRenderer,
        entity : Entity,
        cameraX : Double,
        cameraY : Double,
        cameraZ : Double,
        delta : Float,
        matrices : MatrixStack,
        consumers : VertexConsumerProvider
    ) {
        if(!LavaHack.EVENT_BUS.post(Render3DEvent.WorldRenderer.Render.Entity.Pre(entity, matrices, consumers, cameraX, cameraY, cameraZ, delta)).cancelled) {
            renderEntity(entity, cameraX, cameraY, cameraZ, delta, matrices, consumers)

            Render3DEvent.WorldRenderer.Render.Entity.Post(entity, matrices, consumers, cameraX, cameraY, cameraZ, delta)
        }
    }*/
}