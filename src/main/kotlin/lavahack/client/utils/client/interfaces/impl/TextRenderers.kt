package lavahack.client.utils.client.interfaces.impl

import lavahack.client.features.subsystem.subsystems.FontController
import lavahack.client.features.subsystem.subsystems.NanoVGRenderer
import lavahack.client.utils.client.interfaces.ITextRenderer
import lavahack.client.utils.mc
import lavahack.client.utils.render.screen.*
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import java.awt.Color

val vanillaTextRenderer = VanillaTextRenderer()
val lexenddeca12 = NanoVGTextRenderer("lexenddeca-regular", 12f)
val comfortaab12 = NanoVGTextRenderer("comfortaa-bold", 12f)
val comfortaar12 = NanoVGTextRenderer("comfortaa-regular", 12f)

class VanillaTextRenderer : ITextRenderer {
    override fun drawString(
        context : DrawContext,
        text : String,
        x : Number,
        y : Number,
        color : Color,
        shadow : Boolean
    ) {
        mc.textRenderer.draw(
            text,
            x.toFloat(),
            y.toFloat(),
            color.rgb,
            shadow,
            context.matrices.peek().positionMatrix,
            context.vertexConsumers,
            TextRenderer.TextLayerType.NORMAL,
            0,
            15728880,
            mc.textRenderer.isRightToLeft
        )

        context.draw()
    }

    override fun stringWidth(
        text : String
    ) = mc.textRenderer.getWidth(text)

    override fun fontHeight() = mc.textRenderer.fontHeight
}

class NanoVGTextRenderer(
    private val name : String,
    private val size : Float
) : ITextRenderer {
    init {
        createFont(name, "font/$name.ttf")

        println("created $name  nvg font")
    }

    override fun drawString(
        context : DrawContext,
        text : String,
        x : Number,
        y : Number,
        color : Color,
        shadow : Boolean
    ) {
        if(shadow) {
            drawString(context, text, x.toFloat() + FontController.OFFSET, y.toFloat() + FontController.OFFSET, Color.GRAY, false)
            drawString(context, text, x, y, color, false)
        } else {
            NanoVGRenderer.render {
                text(
                    text,
                    x,
                    y,
                    color,
                    name,
                    size
                )
            }
        }
    }

    override fun stringWidth(
        text : String
    ) = textWidth(text, "font/$name.ttf", size).toInt()

    override fun fontHeight() = size.toInt()
}