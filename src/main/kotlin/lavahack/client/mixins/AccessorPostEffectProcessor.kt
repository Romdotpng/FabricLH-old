package lavahack.client.mixins

import net.minecraft.client.gl.PostEffectPass
import net.minecraft.client.gl.PostEffectProcessor
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 11:42 of 10.08.2023
 */
@Mixin(PostEffectProcessor::class)
interface AccessorPostEffectProcessor {
    @get:Accessor("passes")
    val passes : List<PostEffectPass>
}