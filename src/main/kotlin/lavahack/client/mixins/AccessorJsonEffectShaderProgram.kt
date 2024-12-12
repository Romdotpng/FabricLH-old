package lavahack.client.mixins

import net.minecraft.client.gl.GlUniform
import net.minecraft.client.gl.JsonEffectShaderProgram
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(JsonEffectShaderProgram::class)
interface AccessorJsonEffectShaderProgram {
    @get:Accessor("uniformData")
    val uniformData : List<GlUniform>
}