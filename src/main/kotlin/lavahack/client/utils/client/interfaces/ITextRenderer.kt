package lavahack.client.utils.client.interfaces

import net.minecraft.client.gui.DrawContext
import java.awt.Color

interface ITextRenderer {
    fun drawString(
        context : DrawContext,
        text : String,
        x : Number,
        y : Number,
        color : Color,
        shadow : Boolean = false
    )

    fun stringWidth(
        text : String
    ) : Int

    fun fontHeight() : Int
}