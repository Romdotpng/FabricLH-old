package lavahack.client.utils.client.interfaces.mixins

import net.minecraft.client.gl.Framebuffer

interface IPostEffectProcessor {
    fun addFakeTarget(
        name : String,
        framebuffer : Framebuffer
    )
}