package lavahack.client.mixins

import lavahack.client.event.events.ScreenEvent
import lavahack.client.utils.mc
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Screen::class)
class MixinScreen {
    @Inject(
        method = ["renderBackground"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun renderBackgroundHeadHook(
        context : DrawContext,
        ci : CallbackInfo
    ) {
        if(ScreenEvent.Render.RENDERING && mc.world != null) {
            ci.cancel()
        }
    }
}