package lavahack.client.mixins

import lavahack.client.features.module.modules.render.FullBright
import net.minecraft.client.render.LightmapTextureManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyArgs
import org.spongepowered.asm.mixin.injection.invoke.arg.Args

@Mixin(LightmapTextureManager::class)
class MixinLightmapTextureManager {
    @ModifyArgs(
        method = ["update"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"
        )
    )
    private fun updateSetColorModifyArgs(
        args : Args
    ) {
        if(FullBright.state) {
            args.set(2, -1)
        }
    }
}