package lavahack.client.mixins

import lavahack.client.features.subsystem.subsystems.TimerManager
import net.minecraft.client.render.RenderTickCounter
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(RenderTickCounter::class)
class MixinRenderTickCounter {
    @Shadow
    @JvmField
    var lastFrameDuration = 1f

    @Inject(
        method = ["beginRenderTick"],
        at = [At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J",
            opcode = Opcodes.PUTFIELD
        )]
    )
    private fun beginRenderTickPrevTimeMillisFieldHook(
        time : Long,
        cir : CallbackInfoReturnable<Int>
    ) {
        lastFrameDuration *= TimerManager.multiplier
    }
}