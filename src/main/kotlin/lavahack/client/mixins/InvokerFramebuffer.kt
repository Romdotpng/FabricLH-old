package lavahack.client.mixins

import net.minecraft.client.gl.Framebuffer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(Framebuffer::class)
interface InvokerFramebuffer {
    @Invoker("bind")
    fun bind0(
        updateViewport : Boolean
    )
}