@file:Suppress("KotlinConstantConditions", "UNNECESSARY_NOT_NULL_ASSERTION")

package lavahack.client.utils.render.screen

import lavahack.client.utils.asStream
import lavahack.client.utils.buffer
import lavahack.client.utils.mc
import lavahack.client.utils.minecraft.LavaHackIdentifier
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryStack
import java.awt.Color
import java.nio.ByteBuffer
import java.nio.IntBuffer

private val fontInfos = mutableMapOf<String, STBTTFontinfo>()

var VG = -1L

val Color.vg get()  = NVGColor.calloc().also {
    nvgRGBA(red.toByte(), green.toByte(), blue.toByte(), alpha.toByte(), it)
}!!

fun initVG() {
    VG = nvgCreate(NVG_STENCIL_STROKES)
}

fun prepareVG() {
    nvgBeginFrame(VG, mc.window.scaledWidth.toFloat(), mc.window.scaledHeight.toFloat(), 1f)
}

fun releaseVG() {
    nvgEndFrame(VG)
}

fun rectWH(
    x : Number,
    y : Number,
    w : Number,
    h : Number,
    color : Color
) {
    nvgBeginPath(VG)
    nvgRect(VG, x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat())
    nvgFillColor(VG, color.vg)
    nvgFill(VG)
}

fun roundedRectWH(
    x : Number,
    y : Number,
    w : Number,
    h : Number,
    color : Color,
    radius : Number
) {
    nvgBeginPath(VG)
    nvgRoundedRect(VG, x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), radius.toFloat())
    nvgFillColor(VG, color.vg)
    nvgFill(VG)
}

fun createFont(
    fontName : String,
    fileName : String
) {
    val buffer = LavaHackIdentifier(fileName).asStream().buffer

    nvgCreateFontMem(VG, fontName, buffer, true)
}

fun createFontInfo(
    path : String
) = STBTTFontinfo.create().also {
    val buffer = LavaHackIdentifier(path).asStream().buffer

    if(!stbtt_InitFont(it, buffer as ByteBuffer)) {
        throw IllegalArgumentException("Failed to load $path font")
    }
}!!

fun text(
    text : String,
    x : Number,
    y : Number,
    color : Color,
    name : String,
    size : Float,
    align : Int = NVG_ALIGN_LEFT
) {
    nvgBeginPath(VG)
    nvgFontSize(VG, size)
    nvgFontFace(VG, name)
    nvgFillColor(VG, color.vg)
    nvgTextAlign(VG, align)
    nvgText(VG, x.toFloat(), y.toFloat() + size / 2f + 3f, text)
    nvgFill(VG)
}

fun textWidth(
    text : String,
    path : String,
    size : Float
) = try {
    fun getCodePointSize(
        to : Int,
        i : Int,
        cpOut : IntBuffer
    ) : Int {
        val char1 = text[i]

        if(Character.isHighSurrogate(char1) && i + 1 < to) {
            val char2 = text[i + 1]

            if(Character.isLowSurrogate(char2)) {
                cpOut.put(0, Character.toCodePoint(char1, char2))

                return 2
            }
        }

        cpOut.put(0, char1.code)

        return 1
    }

    val info = fontInfos[path] ?: createFontInfo(path).also { fontInfos[path] = it }
    val stack = MemoryStack.stackPush()
    val pCodePoint = stack.mallocInt(1)
    val pAdvance = stack.mallocInt(1)
    val pLeftSideBearing = stack.mallocInt(1)
    val factor = stbtt_ScaleForMappingEmToPixels(info, size)
    val length = text.length
    var width = 0f
    var i = 0

    while(i < length) {
        i += getCodePointSize(length, i, pCodePoint)

        val codePoint = pCodePoint.get(0)

        stbtt_GetCodepointHMetrics(info, codePoint, pAdvance, pLeftSideBearing)

        width += pAdvance.get(0) * factor
    }

    width
} catch(_ : Throwable) {
    0f
}

