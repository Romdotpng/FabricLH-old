package lavahack.client.mixins

import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gl.PostEffectPass
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Mutable
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(PostEffectPass::class)
interface AccessorPostEffectPass {
    @get:Accessor("input")
    @set:Accessor("input")
    @set:Mutable
    var input : Framebuffer

    @get:Accessor("output")
    @set:Accessor("output")
    @set:Mutable
    var output : Framebuffer
}