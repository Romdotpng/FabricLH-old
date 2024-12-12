@file:Suppress("LocalVariableName")

package lavahack.client.utils.render.screen

import com.mojang.blaze3d.systems.RenderSystem
import lavahack.client.features.subsystem.subsystems.FontController
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.IRect
import lavahack.client.utils.math.RAD
import lavahack.client.utils.math.cos
import lavahack.client.utils.math.sin
import lavahack.client.utils.minecraft.LavaHackIdentifier
import lavahack.client.utils.render.shader.shader
import net.minecraft.client.font.FontStorage
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TrueTypeFontLoader
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Matrix4f
import java.awt.Color
import java.lang.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.ranges.coerceIn

/**
 * @author _kisman_
 * @since 7:28 of 11.05.2023
 */

//section global fields

val textRenderer
    get() = FontController.FONT.valEnum.textRenderer()

val vertexConsumers = VertexConsumerProvider.immediate(BufferBuilder(2048))!!

var scissorContext : ScreenRect? = null

//section global methods

fun prepare(
    context : DrawContext
) {
    enableScissor(scissorContext)
}

fun release(
    context : DrawContext
) {
    disableScissor()
}

//section rects

fun rect(
    context : DrawContext,
    _x1 : Number,
    _y1 : Number,
    _x2 : Number,
    _y2 : Number,
    color : Color
) {
    val matrix4f = context.matrices.peek().positionMatrix

    var x1 = _x1
    var y1 = _y1
    var x2 = _x2
    var y2 = _y2
    var i : Number

    if (x1.toDouble() < x2.toDouble()) {
        i = x1
        x1 = x2
        x2 = i
    }

    if (y1.toDouble() < y2.toDouble()) {
        i = y1
        y1 = y2
        y2 = i
    }

    val red = color.red.toFloat() / 255.0f
    val green = color.green.toFloat() / 255.0f
    val blue = color.blue.toFloat() / 255.0f
    val alpha = color.alpha.toFloat() / 255.0f

    val bufferBuilder = Tessellator.getInstance().buffer

    prepare(context)

    RenderSystem.enableBlend()
    RenderSystem.setShader { shader() }
    bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR)
    bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), 0f).color(red, green, blue, alpha).next()
    bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), 0f).color(red, green, blue, alpha).next()
    bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), 0f).color(red, green, blue, alpha).next()
    bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), 0f).color(red, green, blue, alpha).next()
    tessellator.draw()
    RenderSystem.disableBlend()

    release(context)
}

fun rectWH(
    context : DrawContext,
    x : Number,
    y : Number,
    w : Number,
    h : Number,
    color : Color
) {
    rect(
        context,
        x,
        y,
        x + w,
        y + h,
        color
    )
}

fun rect(
    context : DrawContext,
    rect : IRect,
    color : Color
) {
    rectWH(
        context,
        rect.x,
        rect.y,
        rect.w,
        rect.h,
        color
    )
}

/**
 * @param color1 color of the left top corner
 * @param color2 color of the right top corner
 * @param color3 color of the left bottom corner
 * @param color4 color of the right bottom corner
 */
fun gradientRect(
    context : DrawContext,
    x1 : Number,
    y1 : Number,
    x2 : Number,
    y2 : Number,
    color1 : Color,
    color2 : Color,
    color3 : Color,
    color4 : Color
) {
    val matrix = context.matrices.peek().positionMatrix
    val tessellator = Tessellator.getInstance()
    val builder = tessellator.buffer

    prepare(context)

    RenderSystem.enableBlend()
    RenderSystem.setShader { shader() }
    builder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR)
    builder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0f).color(color1.red, color1.green, color1.blue, color1.alpha).next()
    builder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0f).color(color3.red, color3.green, color3.blue, color3.alpha).next()
    builder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0f).color(color4.red, color4.green, color4.blue, color4.alpha).next()
    builder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0f).color(color2.red, color2.green, color2.blue, color2.alpha).next()
    tessellator.draw()
    RenderSystem.disableBlend()

    release(context)
}

fun gradientRectWH(
    context : DrawContext,
    x : Number,
    y : Number,
    w : Number,
    h : Number,
    color1 : Color,
    color2 : Color,
    color3 : Color,
    color4 : Color
) {
    gradientRect(
        context,
        x,
        y,
        x + w,
        y + h,
        color1,
        color2,
        color3,
        color4
    )
}

fun circleRect(
    context : DrawContext,
    x1 : Number,
    y1 : Number,
    x2 : Number,
    y2 : Number,
    centerX : Number,
    centerY : Number,
    radius1 : Number,
    radius2 : Number = radius1,
    color : Color,
    step : Number = 1.0
) {
    val matrix = context.matrices.peek().positionMatrix

    //TODO: move it into separate class
    val vertexConsumer = object : VertexConsumer {
        override fun vertex(
            x : Double,
            y : Double,
            z : Double
        ) = buffer.vertex(x.coerceIn(x1.toDouble()..x2.toDouble()), y.coerceIn(y1.toDouble()..y2.toDouble()), z)

        override fun color(
            red : Int,
            green : Int,
            blue : Int,
            alpha : Int
        ) = buffer.color(red, green, blue, alpha)

        override fun texture(
            u : Float,
            v : Float
        ) = buffer.texture(u, v)

        override fun overlay(
            u : Int,
            v : Int
        ) = buffer.overlay(u, v)

        override fun light(
            u : Int,
            v : Int
        ) = buffer.light(u, v)

        override fun normal(
            x : Float,
            y : Float,
            z : Float
        ) = buffer.normal(x, y, z)

        override fun next() {
            buffer.next()
        }

        override fun fixedColor(
            red : Int,
            green : Int,
            blue : Int,
            alpha : Int
        ) { }

        override fun unfixColor() { }
    }

    prepare(context)

    RenderSystem.enableBlend()
    RenderSystem.setShader { shader() }

    buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR)

    circle(matrix, vertexConsumer, centerX, centerY, radius1, radius2, color, step)

    tessellator.draw()

    RenderSystem.disableBlend()

    release(context)
}

fun circleRectWH(
    context : DrawContext,
    x : Number,
    y : Number,
    w : Number,
    h : Number,
    centerX : Number,
    centerY : Number,
    radius1 : Number,
    radius2 : Number = radius1,
    color : Color,
    step : Number = 1.0
) {
    circleRect(
        context,
        x,
        y,
        x + w,
        y + h,
        centerX,
        centerY,
        radius1,
        radius2,
        color,
        step
    )
}

fun outlineRect(
    context : DrawContext,
    x1 : Number,
    y1 : Number,
    x2 : Number,
    y2 : Number,
    color : Color,
    width : Number
) {
    //top
    rect(
        context,
        x1,
        y1,
        x2 - width,
        y1 + width,
        color
    )

    //right
    rect(
        context,
        x2 - width,
        y1,
        x2,
        y2,
        color
    )

    //bottom
    rect(
        context,
        x1,
        y2 - width,
        x2 - width,
        y2,
        color
    )

    //left
    rect(
        context,
        x1,
        y1 + width,
        x1 + width,
        y2 - width,
        color
    )
}

fun outlineRectWH(
    context : DrawContext,
    x : Number,
    y : Number,
    w : Number,
    h : Number,
    color : Color,
    width : Number
) {
    outlineRect(
        context,
        x,
        y,
        x + w,
        y + h,
        color,
        width
    )
}

fun outlineRect(
    context : DrawContext,
    rect : IRect,
    color : Color,
    width : Number
) {
    outlineRectWH(
        context,
        rect.x,
        rect.y,
        rect.w,
        rect.h,
        color,
        width
    )
}

//section strings

fun drawString(
    context : DrawContext,
    text : String,
    x : Number,
    y : Number,
    color : Color,
    shadow : Boolean = false
) {
    prepare(context)

    textRenderer.drawString(
        context,
        text,
        x,
        y,
        color,
        shadow
    )

    release(context)
}

fun drawString(
    context : DrawContext,
    text : Text,
    x : Number,
    y : Number,
    color : Color,
    shadow : Boolean = false,
    layer : TextRenderer.TextLayerType = TextRenderer.TextLayerType.NORMAL
) {
    prepare(context)

    mc.textRenderer.draw(
        text,
        x.toFloat(),
        y.toFloat(),
        color.rgb,
        shadow,
        context.matrices.peek().positionMatrix,
        context.vertexConsumers,
        layer,
        0,
        15728880
    )

    context.draw()

    release(context)
}

fun fontHeight() = textRenderer.fontHeight()

fun stringWidth(
    text : String
) = textRenderer.stringWidth(text)

fun createTextRenderer(
    name : String,
    size : Number
) : TextRenderer {
    val font = TrueTypeFontLoader(LavaHackIdentifier("$name.ttf"), size.toFloat(), 2f, TrueTypeFontLoader.Shift(-1f, 0f), "").build().left()

    return if(font.isPresent) {
        val storage = FontStorage(mc.textureManager, Identifier("lavahack"))

        storage.setFonts(listOf(font.get().load(mc.resourceManager)))

        TextRenderer({ storage }, true)
    } else {
        throw IllegalArgumentException("Cannot create text renderer of $name font")
    }
}

//section circles

/**
 * it uses DrawMode.QUAD, but the sector looks like a triangle, so it renders a reversed "triangle" to complete our quad
 */
fun circle(
    matrix : Matrix4f,
    vertexConsumer : VertexConsumer,
    centerX : Number,
    centerY : Number,
    radius1 : Number,
    radius2 : Number = radius1,
    color : Color,
    step : Number = 1.0
) {
    for(i in step.toDouble()..360.0 step step.toDouble()) {
        val cos1 = cos(i * RAD)
        val sin1 = -sin(i * RAD)
        val cos2 = cos((i + step.toDouble()) * RAD)
        val sin2 = -sin((i + step.toDouble()) * RAD)

        val x1 = centerX.toFloat() + cos1 * radius1.toFloat()
        val y1 = centerY.toFloat() + sin1 * radius1.toFloat()
        val x2 = centerX.toFloat() + cos1 * radius2.toFloat()
        val y2 = centerY.toFloat() + sin1 * radius2.toFloat()
        val x3 = centerX.toFloat() + cos2 * radius1.toFloat()
        val y3 = centerY.toFloat() + sin2 * radius1.toFloat()
        val x4 = centerX.toFloat() + cos2 * radius2.toFloat()
        val y4 = centerY.toFloat() + sin2 * radius2.toFloat()


        //current sector
        vertexConsumer.vertex(matrix, x1, y1, 0f).color(color.rgb).next()
        vertexConsumer.vertex(matrix, x2, y2, 0f).color(color.rgb).next()
        vertexConsumer.vertex(matrix, x3, y3, 0f).color(color.rgb).next()
        vertexConsumer.vertex(matrix, x4, y4, 0f).color(color.rgb).next()

        //reversed sector
        vertexConsumer.vertex(matrix, x4, y4, 0f).color(color.rgb).next()
        vertexConsumer.vertex(matrix, x3, y3, 0f).color(color.rgb).next()
        vertexConsumer.vertex(matrix, x2, y2, 0f).color(color.rgb).next()
        vertexConsumer.vertex(matrix, x1, y1, 0f).color(color.rgb).next()
    }
}

fun circle(
    context : DrawContext,
    centerX : Number,
    centerY : Number,
    radius : Number,
    color : Color,
    step : Number = 1.0
) {
    val matrix = context.matrices.peek().positionMatrix

    prepare(context)

    RenderSystem.enableBlend()
    RenderSystem.setShader { shader() }

    buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR)

    circle(matrix, buffer, centerX, centerY, radius, 0.0, color, step)

    tessellator.draw()

    RenderSystem.disableBlend()

    release(context)
}

//section items

fun item(
    context : DrawContext,
    stack : ItemStack,
    x : Number,
    y : Number,
    text : String = stack.count.asString(),
    textColor : Color = Color.WHITE,
    textScale : Number = 1,
    itemScale : Number = 1
) {
    context.matrices.push()
    context.matrices.scale(x, y, itemScale) { context.drawItem(stack, 0, 0) }
    context.matrices.translate(0f, 0f, 232f)
    context.matrices.scale(x + 19 - 2 - stringWidth(text) * textScale, y + 6 + 3 + 9 - fontHeight() * textScale - 1, textScale) { drawString(context, text, 0, 0, textColor) }
    context.matrices.pop()
}

//section draw context

fun MatrixStack.context() = WrappedDrawContext(this, vertexConsumers)

class WrappedDrawContext(
    private val matrices : MatrixStack,
    vertexConsumers : VertexConsumerProvider.Immediate
) : DrawContext(
    mc,
    vertexConsumers
) {
    constructor(
        matrices : MatrixStack,
        buffer : BufferBuilder
    ) : this(
        matrices,
        VertexConsumerProvider.immediate(buffer)
    )

    constructor(
        matrices : MatrixStack
    ) : this(
        matrices,
        BufferBuilder(2048)
    )

    override fun getMatrices() = matrices
}

//section scissors

fun enableScissor(
    _x : Number,
    _y : Number,
    _w : Number,
    _h : Number
) {
    val height = mc.window.framebufferHeight
    val factor = mc.window.scaleFactor
    val x = (factor * _x).toDouble().roundToInt()
    val y = (height - factor * (_y + _h)).toDouble().roundToInt()
    val w = (factor * _w).toDouble().roundToInt()
    val h = (factor * _h).toDouble().roundToInt()

    RenderSystem.enableScissor(x, y, w, h)

    enabledScissor = true
}

fun enableScissor(
    rect : ScreenRect?
) {
    if(rect != null) {
        if(enabledScissor) {
            RenderSystem.disableScissor()
        }

        enableScissor(rect.left, rect.top, rect.width, rect.height)
    }
}

fun disableScissor() {
    if(enabledScissor) {
        RenderSystem.disableScissor()

        enabledScissor = false
    }
}

private var enabledScissor = false

fun setScissor(
    x : Number,
    y : Number,
    w : Number,
    h : Number
) {
    val roundedX = ceil(x.toDouble()).toInt() - 1
    val roundedY = ceil(y.toDouble()).toInt() - 1
    val roundedW = floor((x + w).toDouble()).toInt() - roundedX + 2
    val roundedH = floor((y + h).toDouble()).toInt() - roundedY + 2

    scissorContext = ScreenRect(roundedX, roundedY, roundedW, roundedH)
}

fun resetScissor() {
    scissorContext = null
}