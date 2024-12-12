package lavahack.client.mixins

import lavahack.client.utils.client.interfaces.mixins.IPostEffectProcessor
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gl.PostEffectPass
import net.minecraft.client.gl.PostEffectProcessor
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(PostEffectProcessor::class)
class MixinPostEffectProcessor : IPostEffectProcessor {
    private val fakeFramebuffers = mutableListOf<String>()

    @Shadow
    @Final
    private val targetsByName : Map<String, Framebuffer> = mutableMapOf()

    @Shadow
    @Final
    private val passes : List<PostEffectPass> = mutableListOf()

    override fun addFakeTarget(
        name : String,
        framebuffer : Framebuffer
    ) {
        val prev = targetsByName[name]

        if(prev != framebuffer) {
            for(pass in passes) {
                if(pass.input == prev) {
                    (pass as AccessorPostEffectPass).input = framebuffer
                }

                if(pass.output == prev) {
                    (pass as AccessorPostEffectPass).output = framebuffer
                }
            }

            (targetsByName as HashMap).remove(name)
            fakeFramebuffers.remove(name)
        }

        (targetsByName as HashMap)[name] = framebuffer
        fakeFramebuffers.add(name)
    }

    @Inject(
        method = ["close"],
        at = [At("HEAD")]
    )
    private fun closeHeadHook(
        ci : CallbackInfo
    ) {
        for(fakeFramebuffer in fakeFramebuffers) {
            (targetsByName as HashMap).remove(fakeFramebuffer)
        }
    }
}