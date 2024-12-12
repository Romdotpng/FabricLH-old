package lavahack.client.mixins

import lavahack.client.features.subsystem.subsystems.FontController
import org.spongepowered.asm.mixin.Mixin

@Mixin(
    targets = ["net/minecraft/client/font/UnihexFont\$UnicodeTextureGlyph"]
)
class MixinUnicodeTextureGlyph {
    fun getShadowOffset() = FontController.OFFSET
}