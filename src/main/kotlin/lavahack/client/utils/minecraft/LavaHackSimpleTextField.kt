package lavahack.client.utils.minecraft

import lavahack.client.utils.client.interfaces.IRect
import net.minecraft.SharedConstants
import net.minecraft.client.gui.DrawContext
import org.lwjgl.glfw.GLFW

class LavaHackSimpleTextField(
    private val backgroundText : String = "",
    private val fillCallback : (DrawContext, IRect, Int, Int, focused : Boolean) -> Unit = { _, _, _, _, _ -> },
    private val outlineCallback : (DrawContext, IRect) -> Unit = { _, _ -> },
    private val textCallback : (DrawContext, IRect, text : String, backgroundText : String, focused : Boolean, background : Boolean) -> Unit = { _, _, _, _, _, _ -> },
    private val textSubmitter : (text : String) -> Unit = { }
) {
    var focused = false
    private var rect : IRect? = null

    var text = ""

    fun keyPressed(
        code : Int
    ) {
        if(focused) {
            when(code) {
                GLFW.GLFW_KEY_BACKSPACE -> if(text.isNotEmpty()) {
                    text = text.substring(0, text.length - 1)
                }

                GLFW.GLFW_KEY_ENTER -> {
                    focused = false
                    textSubmitter(text)
                }
            }
        }
    }

    fun charTyped(
        char : Char
    ) {
        if(SharedConstants.isValidChar(char) && focused) {
            text += char
        }
    }

    fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) = (rect != null && mouseX in rect!!.x..(rect!!.x + rect!!.w) && mouseY in rect!!.y..(rect!!.y + rect!!.h)).also {
        focused = if(it) {
            !focused
        } else {
            false
        }

        if(!focused) {
            textSubmitter(text)
        }
    }

    fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        rect : IRect
    ) {
        fillCallback(context, rect, mouseX, mouseY, focused)
        outlineCallback(context, rect)
        textCallback(context, rect, text, backgroundText, focused, text.isEmpty())

        this.rect = rect
    }
}