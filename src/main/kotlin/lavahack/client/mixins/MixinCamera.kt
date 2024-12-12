package lavahack.client.mixins

import lavahack.client.features.module.modules.misc.FreeLook
import lavahack.client.features.module.modules.render.ViewClip
import net.minecraft.client.render.Camera
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyArgs
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.invoke.arg.Args

@Mixin(Camera::class)
class MixinCamera {
    @Inject(
        method = ["clipToSpace"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun clipToSpaceHeadHook(
        desiredCameraDistance : Double,
        cir : CallbackInfoReturnable<Double>
    ) {
        if(ViewClip.state) {
            cir.returnValue = desiredCameraDistance
        }
    }
    @ModifyArgs(
        method = ["update"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"
        )
    )
    fun onUpdateSetRotationArgs(
        args : Args
    ) {
        if (FreeLook.state) {
            args.set(0, FreeLook.yaw)
            args.set(1, FreeLook.pitch)
        }
    }
}