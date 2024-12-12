package lavahack.client.mixins

import lavahack.client.features.subsystem.subsystems.FontController
import org.spongepowered.asm.mixin.Mixin

@Mixin(
    targets = ["net/minecraft/client/font/TrueTypeFont\$TtfGlyph"]
)
class MixinTtfGlyph {
    fun getShadowOffset() = FontController.OFFSET
}