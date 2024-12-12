package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.MinecraftEvent
import lavahack.client.event.events.Render2DEvent
import lavahack.client.event.events.Render3DEvent
import lavahack.client.event.events.ScreenEvent
import lavahack.client.features.module.modules.render.ViewModel
import lavahack.client.utils.render.shader.CORE_SHADERS
import lavahack.client.utils.render.shader.DUMMY_SHADER_PROGRAM
import lavahack.client.utils.render.shader.DummyShaderProgram
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.resource.ResourceFactory
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * @author _kisman_
 * @since 11:40 of 26.05.2023
 */
@Mixin(GameRenderer::class)
class MixinGameRenderer {
    @Inject(
        method = ["renderWorld"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderWorldHeadHook(
        tickDelta : Float,
        limitTime : Long,
        matrices : MatrixStack,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render3DEvent.Pre(matrices, tickDelta)).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["renderWorld"],
        at = [
            At(
                value = "FIELD",
                target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
                opcode = Opcodes.GETFIELD,
                ordinal = 0
            )
        ]
    )
    private fun renderWorldTailHook(
        tickDelta : Float,
        limitTime : Long,
        matrices : MatrixStack,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.Post(matrices, tickDelta))
    }

    @Inject(
        method = ["preloadPrograms"],
        at = [At("TAIL")]
    )
    private fun preloadProgramsTailHook(
        factory : ResourceFactory,
        ci : CallbackInfo
    ) {
        LavaHack.LOGGER.info("Creating core shaders")

        DUMMY_SHADER_PROGRAM = DummyShaderProgram()

        for(shader in CORE_SHADERS) {
            shader.create()
        }
    }

    @Inject(
        method = ["getFov"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun getFovHeadHook(
        camera : Camera,
        tickDelta : Float,
        changingFov : Boolean,
        ci : CallbackInfoReturnable<Double>
    ) {
        if(!changingFov && ViewModel.state && ViewModel.ITEM_FOV_STATE.value) {
            ci.returnValue = ViewModel.ITEM_FOV_VALUE.value.toDouble()
            ci.cancel()
        }
    }

    @Inject(
        method = ["tiltViewWhenHurt"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun tiltViewWhenHurtHeadHook(
        matrices : MatrixStack,
        tickDelta : Float,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render3DEvent.TiltView(), ci)
    }

    @Redirect(
        method = ["render"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;draw()V"
        )
    )
    private fun renderDrawContextDrawRedirectHook(
        context : DrawContext
    ) {
        LavaHack.EVENT_BUS.post(Render2DEvent.AfterScreen(context))

        context.draw()
    }

    @Redirect(
        method = ["render"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"
        )
    )
    private fun renderScreenRenderWithTooltipRedirectHook(
        screen : Screen,
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) {
        ScreenEvent.Render.RENDERING = true

        if(!LavaHack.EVENT_BUS.post(ScreenEvent.Render.Pre(screen, context)).cancelled) {
            screen.renderWithTooltip(context, mouseX, mouseY, delta)

            LavaHack.EVENT_BUS.post(ScreenEvent.Render.Post(screen, context))
        }

        ScreenEvent.Render.RENDERING = false
    }

    @Inject(
        method = ["render"],
        at = [At("TAIL")]
    )
    private fun renderTailHook(
        delta : Float,
        start : Long,
        tick : Boolean,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(MinecraftEvent.GameRenderer.Render.End())
    }
}