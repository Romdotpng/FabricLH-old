package lavahack.client.mixins

import net.minecraft.client.gl.GlUniform
import net.minecraft.client.gl.ShaderProgram
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 19:03 of 12.06.2023
 */
@Mixin(ShaderProgram::class)
interface AccessorShaderProgram {
    @get:Accessor("uniforms")
    @set:Accessor("uniforms")
    var uniforms : List<GlUniform>?
}