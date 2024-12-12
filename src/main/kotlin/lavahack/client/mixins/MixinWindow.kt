package lavahack.client.mixins

import lavahack.client.utils.render.screen.initVG
import net.minecraft.client.WindowEventHandler
import net.minecraft.client.WindowSettings
import net.minecraft.client.util.MonitorTracker
import net.minecraft.client.util.Window
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Window::class)
class MixinWindow {
    @Inject(
        method = ["<init>"],
        at = [At("TAIL")]
    )
    private fun __init__TailHook(
        handler : WindowEventHandler,
        tracker : MonitorTracker,
        settings : WindowSettings,
        mode : String,
        title : String,
        ci : CallbackInfo
    ) {
        initVG()
    }
}