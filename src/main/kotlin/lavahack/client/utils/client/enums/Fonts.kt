package lavahack.client.utils.client.enums

import lavahack.client.utils.client.interfaces.ITextRenderer
import lavahack.client.utils.client.interfaces.impl.*

enum class Fonts(
    val textRenderer : () -> ITextRenderer
) {
    Vanilla({ vanillaTextRenderer }),
    LexendDeca({ lexenddeca12 }),
    Comfortaa({ comfortaar12 }),
    ComfortaaBold({ comfortaab12 })
}
