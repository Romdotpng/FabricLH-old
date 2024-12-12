package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.visible
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import java.awt.Color

/**
 * @author _kisman_
 * @since 10:55 of 08.05.2023
 */
object ColorManager : SubSystem(
    "Color Manager"
) {
    private val COPY_PASTE_ABILITY = register(Setting("Copy Paste Ability", true, "Copy/Paste Ability"))
    private val SYNC_COLOR_ABILITY = register(Setting("Sync Color Ability", true))
    val SYNC_COLOR = register(Setting("Sync Color", Colour(255, 0, 0, 255, true)))

    private val PRIMARY_PREFIX_COLOR = register(Setting("Primary Prefix Color", Colour(-1)) {
        CHAT_PREFIX = it.value.formatting
    })

    val COPY_PASTE_VISIBILITY = visible { COPY_PASTE_ABILITY.value }
    val SYNC_COLOR_VISIBILITY = visible { SYNC_COLOR_ABILITY.value }

    var CHAT_PREFIX = WHITE
    val CHAT_ENABLE = GREEN
    val CHAT_DISABLE = RED
    val CHAT_INFO = GOLD

    var COPIED_COLOR : Color? = null
}

fun color(
    color : Int
) = TextColor.fromRgb(color)!!

val WHITE = TextColor.fromFormatting(Formatting.WHITE)!!
val GREEN = TextColor.fromFormatting(Formatting.GREEN)!!
val DARK_GREEN = TextColor.fromFormatting(Formatting.DARK_GREEN)!!
val RED = TextColor.fromFormatting(Formatting.RED)!!
val DARK_RED = TextColor.fromFormatting(Formatting.DARK_RED)!!
val BLUE = TextColor.fromFormatting(Formatting.BLUE)!!
val DARK_BLUE = TextColor.fromFormatting(Formatting.DARK_BLUE)
val GRAY = TextColor.fromFormatting(Formatting.GRAY)!!
val BLACK = TextColor.fromFormatting(Formatting.BLACK)!!
val AQUA = TextColor.fromFormatting(Formatting.AQUA)!!
val GOLD = TextColor.fromFormatting(Formatting.GOLD)!!

fun formatted(
    text : String,
    formatting : Formatting
) = Text.literal(text).formatted(formatting)!!

fun colored(
    text : String,
    color : TextColor
) = Text.literal(text).setStyle(Style.EMPTY.withColor(color))!!

fun colored(
    text : MutableText,
    color : TextColor
) = text.setStyle(Style.EMPTY.withColor(color))!!

//TODO: compare with Colour.formatting
fun Color.text() = TextColor.fromRgb(this.rgb)!!