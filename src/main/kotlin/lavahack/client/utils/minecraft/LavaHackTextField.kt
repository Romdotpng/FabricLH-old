package lavahack.client.utils.minecraft

//TODO: finish it

/*import lavahack.client.utils.client.interfaces.IDraggable
import lavahack.client.utils.client.interfaces.impl.Rect
import lavahack.client.utils.mc
import net.minecraft.SharedConstants
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.MathHelper
import kotlin.math.max
import kotlin.math.min*/

/*
class LavaHackTextField(
    private val textRenderer : () -> TextRenderer = { mc.textRenderer },
    private val maxLength : Int = 100,
    private val backgroundText : String = "",
    private val fillCallback : (DrawContext, IDraggable, selected : Boolean) -> Unit = { _, _, _ -> },
    private val outlineCallback : (DrawContext, IDraggable) -> Unit = { _, _ -> },
    private val textColor : (selected : Boolean, background : Boolean) -> Unit = { _, _ -> }
) {
    var selecting = false

    private var firstCharIndex = 0

    private var selectionStart = 0
        set(value) {
            field = MathHelper.clamp(value, 0, text.length)
        }


    private var selectionEnd = 0
        set(value) {
            val length = text.length

            field = MathHelper.clamp(value, 0, length)

            if(firstCharIndex > length) {
                firstCharIndex = i
            }


        }

    private var text = ""
        set(value) {
            field = if(value.length > maxLength) {
                value.substring(0, maxLength)
            } else {
                value
            }

            cursorToEnd()
            selectionEnd(selectionStart)
        }

    private val selectedText get() = text.substring(min(selectionStart, selectionEnd), max(selectionStart, selectionEnd))

    fun write(
        text : String
    ) {
        val selectionStart = min(selectionStart, selectionEnd)
        val selectionEnd = max(selectionStart, selectionEnd)
        val diff = maxLength - text.length - (selectionStart - selectionEnd)
        var string = SharedConstants.stripInvalidChars(text)
        var length = string.length

        if(diff < length) {
            string = string.substring(0, diff)
            length = diff
        }

        val written = StringBuilder(text).replace(selectionStart, selectionEnd, string).toString()

        text = written
        selectionStart



    }

    fun render(
        context : DrawContext,
        x : Number,
        y : Number,
        w : Number,
        h : Number
    ) {
        val rect = Rect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat())

    }
}*/
