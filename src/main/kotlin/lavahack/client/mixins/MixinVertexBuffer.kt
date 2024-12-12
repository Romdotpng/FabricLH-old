package lavahack.client.mixins

import lavahack.client.utils.minecraft.LavaHackShaderProgram
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.gl.VertexBuffer
import org.joml.Matrix4f
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(VertexBuffer::class)
class MixinVertexBuffer {
    @Inject(
        method = ["drawInternal"],
        at = [At("HEAD")]
    )
    private fun drawInternalHeadHook(
        modelViewMat : Matrix4f,
        projMat : Matrix4f,
        program : ShaderProgram,
        ci : CallbackInfo
    ) {
        if(program is LavaHackShaderProgram) {
            val shader = program.shader

            shader.defaultSamplers()
            shader.externalUniforms()
        }
    }
}