package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.Render3DEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameOverlayRenderer
import net.minecraft.client.util.math.MatrixStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(InGameOverlayRenderer::class)
class MixinInGameOverlayRenderer {
    private companion object {
        @JvmStatic
        @Inject(
            method = ["renderUnderwaterOverlay"],
            at = [At("HEAD")],
            cancellable = true
        )
        private fun renderUnderwaterOverlayHeadHook(
            mc : MinecraftClient,
            matrices : MatrixStack,
            ci : CallbackInfo
        ) {
            LavaHack.EVENT_BUS.post(Render3DEvent.Overlay.Underwater(), ci)
        }

        @JvmStatic
        @Inject(
            method = ["renderFireOverlay"],
            at = [At("HEAD")],
            cancellable = true
        )
        private fun renderFireOverlayHeadHook(
            mc : MinecraftClient,
            matrices : MatrixStack,
            ci : CallbackInfo
        ) {
            LavaHack.EVENT_BUS.post(Render3DEvent.Overlay.Fire(), ci)
        }
    }
}