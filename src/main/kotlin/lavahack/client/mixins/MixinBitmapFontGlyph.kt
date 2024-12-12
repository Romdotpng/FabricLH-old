package lavahack.client.mixins

import lavahack.client.features.subsystem.subsystems.FontController
import org.spongepowered.asm.mixin.Mixin

@Mixin(
    targets = ["net/minecraft/client/font/BitmapFont\$BitmapFontGlyph"]
)
class MixinBitmapFontGlyph {
    fun getShadowOffset() = FontController.OFFSET
}