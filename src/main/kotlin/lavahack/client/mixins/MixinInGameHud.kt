package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.Render2DEvent
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 19:30 of 08.05.2023
 */
@Mixin(InGameHud::class)
class MixinInGameHud {
    @Inject(
        method = ["render"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderHeadHook(
        context : DrawContext,
        delta : Float,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render2DEvent.Pre(context, delta)).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["render"],
        at = [At("TAIL")]
    )
    private fun renderTailHook(
        context : DrawContext,
        delta : Float,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(Render2DEvent.Post(context, delta))
    }

    @Inject(
        method = ["renderPortalOverlay"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderPortalOverlayHeadHook(
        context : DrawContext,
        nauseaStrength : Float,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render2DEvent.Overlay.Portal()).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["renderCrosshair"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderCrosshairHeadHook(
        context : DrawContext,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render2DEvent.Overlay.Crosshair()).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["renderSpyglassOverlay"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderSpyglassOverlayHeadHook(
        context : DrawContext,
        scale : Float,
        ci : CallbackInfo
    ) {
        if(LavaHack.EVENT_BUS.post(Render2DEvent.Overlay.Spyglass()).cancelled) {
            ci.cancel()
        }
    }

    @Inject(
        method = ["renderOverlay"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderOverlayHeadHook(
        context : DrawContext,
        texture : Identifier,
        opacity : Float,
        ci : CallbackInfo
    ) {
        if(
            when(texture) {
                PUMPKIN_BLUR -> LavaHack.EVENT_BUS.post(Render2DEvent.Overlay.Pumpkin())
                POWDER_SNOW_OUTLINE -> LavaHack.EVENT_BUS.post(Render2DEvent.Overlay.PowderSnow())
                else -> null
            }?.cancelled == true
        ) {
            ci.cancel()
        }
    }

    private companion object {
        @JvmField
        @Shadow
        @Final
        var PUMPKIN_BLUR : Identifier? = null

        @JvmField
        @Shadow
        @Final
        var POWDER_SNOW_OUTLINE : Identifier? = null
    }
}