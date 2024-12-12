package lavahack.client.utils.render.world

import com.mojang.blaze3d.systems.RenderSystem
import lavahack.client.features.subsystem.subsystems.DevelopmentSettings
import lavahack.client.settings.pattern.patterns.SlideRenderingPattern
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.impl.BoxColorer
import lavahack.client.utils.client.interfaces.impl.ColoredBox
import lavahack.client.utils.math.*
import lavahack.client.utils.render.screen.rectWH
import lavahack.client.utils.render.shader.resetShader
import lavahack.client.utils.render.shader.setShader
import lavahack.client.utils.render.shader.shader
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f
import java.awt.Color

/**
 * @author _kisman_
 * @since 9:51 of 26.05.2023
 */

//section box renderer

fun full(
    matrices : MatrixStack,
    box : Box,
    fill : BoxColorer?,
    outline : BoxColorer?,
    width : Number
) {
    if(fill != null) {
        fill(
            matrices,
            box,
            fill.color1.value,
            fill.color2.value,
            fill.color3.value,
            fill.color4.value,
            fill.color5.value,
            fill.color6.value,
            fill.color7.value,
            fill.color8.value
        )
    }

    if(outline != null) {
        outline(
            matrices,
            box,
            outline.color1.value,
            outline.color2.value,
            outline.color3.value,
            outline.color4.value,
            outline.color5.value,
            outline.color6.value,
            outline.color7.value,
            outline.color8.value,
            width
        )
    }
}

fun box(
    matrices : MatrixStack,
    box : Box,
    color1 : Color,
    color2 : Color,
    color3 : Color,
    color4 : Color,
    color5 : Color,
    color6 : Color,
    color7 : Color,
    color8 : Color,
    mode : VertexFormat.DrawMode,
    format : VertexFormat,
    fill : Boolean,
    width : Number
) {
    RenderSystem.lineWidth(width.toFloat())
    RenderSystem.blendFunc(770, 1)

    prepare()

    buffer.begin(mode, format)

    box.setup(
        buffer,
        matrices,
        color1,
        color2,
        color3,
        color4,
        color5,
        color6,
        color7,
        color8,
        fill
    )

    tessellator.draw()

    release()
}

fun fill(
    matrices : MatrixStack,
    box : Box,
    color1 : Color,
    color2 : Color,
    color3 : Color,
    color4 : Color,
    color5 : Color,
    color6 : Color,
    color7 : Color,
    color8 : Color,
) {
    box(
        matrices,
        box,
        color1,
        color2,
        color3,
        color4,
        color5,
        color6,
        color7,
        color8,
        VertexFormat.DrawMode.QUADS,
        VertexFormats.POSITION_COLOR,
        true,
        0
    )
}

fun outline(
    matrices : MatrixStack,
    box : Box,
    color1 : Color,
    color2 : Color,
    color3 : Color,
    color4 : Color,
    color5 : Color,
    color6 : Color,
    color7 : Color,
    color8 : Color,
    width : Number
) {
    setShader(GameRenderer.getRenderTypeLinesProgram())

    box(
        matrices,
        box,
        color1,
        color2,
        color3,
        color4,
        color5,
        color6,
        color7,
        color8,
        VertexFormat.DrawMode.LINES,
        VertexFormats.LINES,
        false,
        width
    )

    resetShader()
}

//section box utils

fun Box.correct() = this.offset(mc.entityRenderDispatcher.camera.pos.negate())!!

fun Vec3d.correct() = this.add(mc.entityRenderDispatcher.camera.pos.negate())!!

fun Box.scale(
    coeff : Double
) : Box {
    val offsetX = coeff * (maxX - minX) / 2
    val offsetY = coeff * (maxY - minY) / 2
    val offsetZ = coeff * (maxZ - minZ) / 2

    val center = center

    return Box(
        center.x - offsetX,
        center.y - offsetY,
        center.z - offsetZ,
        center.x + offsetX,
        center.y + offsetY,
        center.z + offsetZ
    )
}

fun Box.setup(
    builder : BufferBuilder,
    matrices : MatrixStack,
    color1 : Color,
    color2 : Color,
    color3 : Color,
    color4 : Color,
    color5 : Color,
    color6 : Color,
    color7 : Color,
    color8 : Color,
    fill : Boolean
) {
    val matrix4f = matrices.peek().positionMatrix
    val matrix3f = matrices.peek().normalMatrix
    val minX = this.minX.toFloat()
    val minY = this.minY.toFloat()
    val minZ = this.minZ.toFloat()
    val maxX = this.maxX.toFloat()
    val maxY = this.maxY.toFloat()
    val maxZ = this.maxZ.toFloat()

    if(fill) {
        builder.vertex(matrix4f, minX, minY, minZ).color(color1.rgb).next()
        builder.vertex(matrix4f, maxX, minY, minZ).color(color2.rgb).next()
        builder.vertex(matrix4f, maxX, minY, maxZ).color(color3.rgb).next()
        builder.vertex(matrix4f, minX, minY, maxZ).color(color4.rgb).next()
        builder.vertex(matrix4f, minX, maxY, minZ).color(color5.rgb).next()
        builder.vertex(matrix4f, minX, maxY, maxZ).color(color8.rgb).next()
        builder.vertex(matrix4f, maxX, maxY, maxZ).color(color7.rgb).next()
        builder.vertex(matrix4f, maxX, maxY, minZ).color(color6.rgb).next()
        builder.vertex(matrix4f, minX, minY, minZ).color(color1.rgb).next()
        builder.vertex(matrix4f, minX, maxY, minZ).color(color5.rgb).next()
        builder.vertex(matrix4f, maxX, maxY, minZ).color(color6.rgb).next()
        builder.vertex(matrix4f, maxX, minY, minZ).color(color2.rgb).next()
        builder.vertex(matrix4f, maxX, minY, minZ).color(color2.rgb).next()
        builder.vertex(matrix4f, maxX, maxY, minZ).color(color6.rgb).next()
        builder.vertex(matrix4f, maxX, maxY, maxZ).color(color7.rgb).next()
        builder.vertex(matrix4f, maxX, minY, maxZ).color(color3.rgb).next()
        builder.vertex(matrix4f, minX, minY, maxZ).color(color4.rgb).next()
        builder.vertex(matrix4f, maxX, minY, maxZ).color(color3.rgb).next()
        builder.vertex(matrix4f, maxX, maxY, maxZ).color(color7.rgb).next()
        builder.vertex(matrix4f, minX, maxY, maxZ).color(color8.rgb).next()
        builder.vertex(matrix4f, minX, minY, minZ).color(color1.rgb).next()
        builder.vertex(matrix4f, minX, minY, maxZ).color(color4.rgb).next()
        builder.vertex(matrix4f, minX, maxY, maxZ).color(color8.rgb).next()
        builder.vertex(matrix4f, minX, maxY, minZ).color(color5.rgb).next()
    } else {
        builder.vertex(matrix4f, minX, minY, minZ).color(color1.rgb).normal(matrix3f, 1.0F, 0.0F, 0.0F).next()
        builder.vertex(matrix4f, maxX, minY, minZ).color(color2.rgb).normal(matrix3f, 1.0F, 0.0F, 0.0F).next()
        builder.vertex(matrix4f, minX, minY, minZ).color(color1.rgb).normal(matrix3f, 0.0F, 1.0F, 0.0F).next()
        builder.vertex(matrix4f, minX, maxY, minZ).color(color5.rgb).normal(matrix3f, 0.0F, 1.0F, 0.0F).next()
        builder.vertex(matrix4f, minX, minY, minZ).color(color1.rgb).normal(matrix3f, 0.0F, 0.0F, 1.0F).next()
        builder.vertex(matrix4f, minX, minY, maxZ).color(color4.rgb).normal(matrix3f, 0.0F, 0.0F, 1.0F).next()
        builder.vertex(matrix4f, maxX, minY, minZ).color(color2.rgb).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, maxY, minZ).color(color6.rgb).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, maxY, minZ).color(color6.rgb).normal(matrix3f, -1.0f, 0.0f, 0.0f).next()
        builder.vertex(matrix4f, minX, maxY, minZ).color(color5.rgb).normal(matrix3f, -1.0f, 0.0f, 0.0f).next()
        builder.vertex(matrix4f, minX, maxY, minZ).color(color5.rgb).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        builder.vertex(matrix4f, minX, maxY, maxZ).color(color8.rgb).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        builder.vertex(matrix4f, minX, maxY, maxZ).color(color8.rgb).normal(matrix3f, 0.0f, -1.0f, 0.0f).next()
        builder.vertex(matrix4f, minX, minY, maxZ).color(color4.rgb).normal(matrix3f, 0.0f, -1.0f, 0.0f).next()
        builder.vertex(matrix4f, minX, minY, maxZ).color(color4.rgb).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, minY, maxZ).color(color3.rgb).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, minY, maxZ).color(color3.rgb).normal(matrix3f, 0.0f, 0.0f, -1.0f).next()
        builder.vertex(matrix4f, maxX, minY, minZ).color(color2.rgb).normal(matrix3f, 0.0f, 0.0f, -1.0f).next()
        builder.vertex(matrix4f, minX, maxY, maxZ).color(color2.rgb).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, maxY, maxZ).color(color7.rgb).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, minY, maxZ).color(color3.rgb).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, maxY, maxZ).color(color7.rgb).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        builder.vertex(matrix4f, maxX, maxY, minZ).color(color6.rgb).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        builder.vertex(matrix4f, maxX, maxY, maxZ).color(color7.rgb).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
    }
}

//section default stuff

fun prepare() {
    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.disableDepthTest()
    RenderSystem.depthMask(false)
    RenderSystem.disableCull()
    RenderSystem.setShader { shader() }
}

fun release() {
    RenderSystem.disableBlend()
    RenderSystem.enableDepthTest()
    RenderSystem.depthMask(true)
    RenderSystem.enableCull()
}

fun lines() = if(DevelopmentSettings.GL_DEBUG_LINES.value) {
    VertexFormat.DrawMode.DEBUG_LINES
} else {
    VertexFormat.DrawMode.LINES
}

fun lineStrip() = if(DevelopmentSettings.GL_DEBUG_LINES.value) {
    VertexFormat.DrawMode.DEBUG_LINE_STRIP
} else {
    VertexFormat.DrawMode.LINE_STRIP
}

//section slide renderer
class SlideRenderer {
    private val cache = HashMap<Box, Long>()

    private var current : Box? = null
    private var prev : Box? = null
    private var prev2 : Box? = null

    private var rendered : Box? = null

    private var last : Box? = null
    private var timestamp = 0L

    fun reset() {
        cache.clear()
        current = null
        prev = null
        rendered = null
        last = null
        timestamp = 0L
    }

    fun handleRender(
        matrices : MatrixStack,
        box : Box?,
        renderer : SlideRenderingPattern,
        movingLength : Float = renderer.MOVING_LENGTH.value.toFloat(),
        fadeLength : Float = renderer.FADE_LENGTH.value.toFloat(),
        alphaFade : Boolean = renderer.ALPHA_FADE.value,
        boxFade : Boolean = renderer.BOX_FADE.value,
        boxModifier : (Box) -> Box = { it }
    ) {
        update(box, renderer)
        render(matrices, movingLength, fadeLength, alphaFade, boxFade, renderer, boxModifier)
    }

    fun update(
        box : Box?,
        renderer : SlideRenderingPattern
    ) {
        if(box != null) {
            val colored = ColoredBox(
                box.minX,
                box.minY,
                box.minZ,
                box.maxX,
                box.maxY,
                box.maxZ,
                renderer.fillColorer.clone(),
                renderer.outlineColorer.clone()
            )

            cache[colored] = System.currentTimeMillis()
        } else if(prev != null && !cache.contains(prev!!)) {
            val colored = ColoredBox(
                prev!!.minX,
                prev!!.minY,
                prev!!.minZ,
                prev!!.maxX,
                prev!!.maxY,
                prev!!.maxZ,
                renderer.fillColorer.clone(),
                renderer.outlineColorer.clone()
            )

            cache[colored] = System.currentTimeMillis()
        }

        if(box != last) {
            current = box
            prev = rendered ?: current
            prev2 = prev
            rendered = null
            timestamp = System.currentTimeMillis()

            last = box
        } else {
            prev = null
        }
    }

    fun render(
        matrices : MatrixStack,
        movingLength : Float,
        fadeLength : Float,
        alphaFade : Boolean,
        boxFade : Boolean,
        renderer : SlideRenderingPattern,
        boxModifier : (Box) -> Box
    ) {
        val multiplier = multiplier(movingLength, renderer)

        prev2?.let { prev ->
            current?.let { current ->
                val minX = lerp(prev.minX, current.minX, multiplier)
                val minY = lerp(prev.minY, current.minY, multiplier)
                val minZ = lerp(prev.minZ, current.minZ, multiplier)
                val maxX = lerp(prev.maxX, current.maxX, multiplier)
                val maxY = lerp(prev.maxY, current.maxY, multiplier)
                val maxZ = lerp(prev.maxZ, current.maxZ, multiplier)

                val box = Box(minX, minY, minZ, maxX, maxY, maxZ)

                renderer.draw(matrices, boxModifier(box))

                rendered = box
            }
        }

        if(fadeLength != 0f && (alphaFade || boxFade)) {
            for(entry in cache.toMutableMap()) {
                val box = entry.key
                val timestamp = entry.value
                val alphaCoeff = fade(fadeLength, renderer, timestamp)

                if(alphaCoeff == 0.0) {
                    cache.remove(box)
                }

                if((multiplier == 0.0 && box == current) || alphaCoeff == 0.0) {
                    continue
                }

                if(box is ColoredBox) {
                    if (alphaFade) {
                        renderer.updateColorers(box.fillColorer.clone().applyAlphaCoeff(alphaCoeff), box.outlineColorer.clone().applyAlphaCoeff(alphaCoeff))
                    } else {
                        renderer.updateColorers(box.fillColorer, box.outlineColorer)
                    }
                } else {
                    renderer.updateColorers()
                }

                renderer.draw(
                    matrices,
                    if(boxFade) {
                        mutate(alphaCoeff, renderer, box)
                    } else {
                        box
                    },
                    update = false
                )

                renderer.updateColorers()
            }
        }
    }

    private fun multiplier(
        length : Float,
        renderer : SlideRenderingPattern
    ) = if(length != 0f) {
        renderer.MOVING_EASING.valEnum.inc(delta(timestamp, length))
    } else {
        1.0
    }

    private fun fade(
        length : Float,
        renderer : SlideRenderingPattern,
        time : Long
    ) = if(length != 0f) {
        renderer.FADE_EASING.valEnum.dec(delta(time, length))
    } else {
        1.0
    }

    private fun mutate(
        coeff : Double,
        renderer : SlideRenderingPattern,
        box : Box
    ) = renderer.BOX_FADE_LOGIC.valEnum.modifier(box, coeff)
}

//section strip lines

fun line(
    matrices : MatrixStack,
    points : Map<Vec3d, Color>,
    width : Number
) {
    val matrix4f = matrices.peek().positionMatrix
    val matrix3f = matrices.peek().normalMatrix

    setShader(GameRenderer.getRenderTypeLinesProgram())

    RenderSystem.lineWidth(width.toFloat())
    RenderSystem.blendFunc(770, 1)

    prepare()

    buffer.begin(lines(), VertexFormats.LINES)

    var prev : Pair<Vector3f, Color>? = null

    for((vec, color) in points) {
        val corrected = vec.correct().toVector3f()

        if(prev != null) {
            val normals = corrected.normalize()
            val prevVec = prev.first
            val prevColor = prev.second

            buffer.vertex(matrix4f, prevVec.x, prevVec.y, prevVec.z).color(prevColor.rgb).normal(matrix3f, normals.x, normals.y, normals.z).next()
            buffer.vertex(matrix4f, corrected.x, corrected.y, corrected.z).color(color.rgb).normal(matrix3f, normals.x, normals.y, normals.z).next()
        }

        prev = corrected to color
    }

    tessellator.draw()

    release()
    resetShader()
}

//section tracers

fun tracer(
    matrices : MatrixStack,
    pos : Vec3d,
    color : Color,
    width : Number
) {
    val matrix4f = matrices.peek().positionMatrix
    val matrix3f = matrices.peek().normalMatrix

    val camera = mc.gameRenderer.camera
    val yaw = camera.yaw * -RAD
    val pitch = camera.pitch * -RAD
    val eyes = Vec3d(0.0, 0.0, 1.0).rotateX(pitch).rotateY(yaw)

    setShader(GameRenderer.getRenderTypeLinesProgram())

    RenderSystem.lineWidth(width.toFloat())
    RenderSystem.blendFunc(770, 1)

    prepare()

    buffer.begin(lines(), VertexFormats.LINES)

    buffer.vertex(matrix4f, eyes).color(color.rgb).normal(matrix3f, eyes.normalize()).next()
    buffer.vertex(matrix4f, pos.correct()).color(color.rgb).normal(matrix3f, pos.normalize()).next()

    tessellator.draw()

    release()
    resetShader()
}

/**
 * @param color1 inside color
 * @param color2 outside color
 */
fun arrow(
    matrices : MatrixStack,
    vec : Vec3d,
    color1 : Color,
    color2 : Color,
    width : Number,
    range : Number
) {
    val rotation = mc.entityRenderDispatcher.rotation
    val camera = mc.gameRenderer.camera
    val yaw = camera.yaw * -RAD
    val pitch = camera.pitch * -RAD
    val eyes = Vec3d(0.0, 0.0, 1.0).rotateX(pitch).rotateY(yaw)
    val vec1 = vec.correct().subtract(eyes).normalize().multiply(range.toDouble())
    val x = vec1.x
    val y = vec1.y
    val z = vec1.z

    matrices.push()
    matrices.translate(x, y, z)
    matrices.pop()
    matrices.multiply(rotation)

    rectWH(
        vec1.x,
        vec1.y,
        20,
        20,
        color1
    )

    matrices.pop()
    matrices.pop()
}

//section circles
//TODO: circle with custom rotations

fun horizontalCircle(
    matrices : MatrixStack,
    centre : Vec3d,
    color : Color,
    width : Number,
    radius : Number,
    step : Number = 1.0
) {
    val matrix4f = matrices.peek().positionMatrix
    val matrix3f = matrices.peek().normalMatrix
    val y = centre.y

    setShader(GameRenderer.getRenderTypeLinesProgram())

    RenderSystem.lineWidth(width.toFloat())
    RenderSystem.blendFunc(770, 1)

    prepare()

    buffer.begin(lineStrip(), VertexFormats.LINES)

    for(i in step.toDouble()..360.0 step step.toDouble()) {
        val x = centre.x.toFloat() + cos(i * RAD) * radius.toDouble()
        val z = centre.z.toFloat() - sin(i * RAD) * radius.toDouble()
        val vec = Vec3d(x, y, z)
        val normal = vec.normalize()

        buffer.vertex(matrix4f, vec.correct()).color(color.rgb).normal(matrix3f, normal).next()
    }

    tessellator.draw()

    release()
    resetShader()
}

//TODO: compare with previous horizontalCircle method
//TODO: use ScreenRendering::circle
fun gradientHorizontalCircle(
    matrices : MatrixStack,
    centre : Vec3d,
    color1 : Color,
    color2 : Color,
    radius1 : Number,
    radius2 : Number,
    step : Number = 1.0,
    counter : Int = 0
) {
    val matrix = matrices.peek().positionMatrix
    val centerX = centre.x
    val centerZ = centre.z
    val y = centre.y + 0.01 + 0.00001 * counter

    RenderSystem.enableBlend()
    RenderSystem.setShader { shader() }

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)

    for(i in step.toDouble()..360.0 step step.toDouble()) {
        val cos1 = cos(i * RAD)
        val sin1 = -sin(i * RAD)
        val cos2 = cos((i + step.toDouble()) * RAD)
        val sin2 = -sin((i + step.toDouble()) * RAD)

        val x1 = centerX.toFloat() + cos1 * radius1.toDouble()
        val z1 = centerZ.toFloat() + sin1 * radius1.toDouble()
        val x2 = centerX.toFloat() + cos1 * radius2.toDouble()
        val z2 = centerZ.toFloat() + sin1 * radius2.toDouble()
        val x3 = centerX.toFloat() + cos2 * radius1.toDouble()
        val z3 = centerZ.toFloat() + sin2 * radius1.toDouble()
        val x4 = centerX.toFloat() + cos2 * radius2.toDouble()
        val z4 = centerZ.toFloat() + sin2 * radius2.toDouble()

        val pos1 = Vec3d(x1, y, z1).correct()
        val pos2 = Vec3d(x2, y, z2).correct()
        val pos3 = Vec3d(x3, y, z3).correct()
        val pos4 = Vec3d(x4, y, z4).correct()

        //current sector
        buffer.vertex(matrix, pos1).color(color1.rgb).next()
        buffer.vertex(matrix, pos2).color(color2.rgb).next()
        buffer.vertex(matrix, pos3).color(color1.rgb).next()
        buffer.vertex(matrix, pos4).color(color2.rgb).next()

        //reversed sector
        buffer.vertex(matrix, pos4).color(color2.rgb).next()
        buffer.vertex(matrix, pos3).color(color1.rgb).next()
        buffer.vertex(matrix, pos2).color(color2.rgb).next()
        buffer.vertex(matrix, pos1).color(color1.rgb).next()
    }

    tessellator.draw()

    RenderSystem.disableBlend()
}

fun gradientBlendCircleWithAlpha(
    matrices : MatrixStack,
    centre : Vec3d,
    color1 : Color,
    color2 : Color,
    radius1 : Number,
    radius2 : Number,
    step : Number = 1.0,
    counter : Int = 0
) {
    val matrix = matrices.peek().positionMatrix
    val centerX = centre.x
    val centerZ = centre.z
    val y = centre.y + 0.01 + 0.00001 * counter

    RenderSystem.enableBlend()
    RenderSystem.setShader { shader() }

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)

    for (i in 0.0..360.0 step step.toDouble()) {
        val percent = i / 360.0
        val progress = (if(percent > 0.5) 1.0 - percent else percent) * 2.0
        val color = lerp(color2, color1, progress)//mix(colorProgress.toFloat(), color2, color1)

        val cos1 = cos(i * RAD)
        val sin1 = -sin(i * RAD)
        val cos2 = cos((i + step.toDouble()) * RAD)
        val sin2 = -sin((i + step.toDouble()) * RAD)

        val x1 = centerX.toFloat() + cos1 * radius1.toDouble()
        val z1 = centerZ.toFloat() + sin1 * radius1.toDouble()
        val x2 = centerX.toFloat() + cos1 * radius2.toDouble()
        val z2 = centerZ.toFloat() + sin1 * radius2.toDouble()
        val x3 = centerX.toFloat() + cos2 * radius1.toDouble()
        val z3 = centerZ.toFloat() + sin2 * radius1.toDouble()
        val x4 = centerX.toFloat() + cos2 * radius2.toDouble()
        val z4 = centerZ.toFloat() + sin2 * radius2.toDouble()

        val pos1 = Vec3d(x1, y, z1).correct()
        val pos2 = Vec3d(x2, y, z2).correct()
        val pos3 = Vec3d(x3, y, z3).correct()
        val pos4 = Vec3d(x4, y, z4).correct()

        //current sector
        buffer.vertex(matrix, pos1).color(color.alpha(color1.alpha).rgb).next()
        buffer.vertex(matrix, pos2).color(color.alpha(color2.alpha).rgb).next()
        buffer.vertex(matrix, pos3).color(color.alpha(color1.alpha).rgb).next()
        buffer.vertex(matrix, pos4).color(color.alpha(color2.alpha).rgb).next()

        //reversed sector
        buffer.vertex(matrix, pos4).color(color.alpha(color2.alpha).rgb).next()
        buffer.vertex(matrix, pos3).color(color.alpha(color1.alpha).rgb).next()
        buffer.vertex(matrix, pos2).color(color.alpha(color2.alpha).rgb).next()
        buffer.vertex(matrix, pos1).color(color.alpha(color1.alpha).rgb).next()
    }
    tessellator.draw()

    RenderSystem.disableBlend()
}