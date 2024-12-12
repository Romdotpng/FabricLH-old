@file:Suppress("UNCHECKED_CAST", "LocalVariableName", "CAST_NEVER_SUCCEEDS")

package lavahack.client.utils.render.shader

import com.google.common.collect.ImmutableMap
import com.google.gson.JsonArray
import com.mojang.blaze3d.systems.RenderSystem
import lavahack.client.LavaHack
import lavahack.client.event.bus.listener
import lavahack.client.event.events.WindowEvent
import lavahack.client.features.subsystem.subsystems.GaussianBlurRenderer
import lavahack.client.mixins.AccessorJsonEffectShaderProgram
import lavahack.client.mixins.AccessorPostEffectProcessor
import lavahack.client.mixins.AccessorShaderProgram
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.IShader
import lavahack.client.utils.client.interfaces.impl.SettingRegistry
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.mixins.IPostEffectProcessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.*
import net.minecraft.client.render.*
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import org.lwjgl.opengl.GL11

/**
 * *PostProcessShader* class is implementation of PostEffectProcessor
 *
 * *Shader* class is implementation of ShaderProgram
 *
 * @author _kisman_
 * @since 6:25 of 16.05.2023
 */

//section default stuff

var DUMMY_SHADER_PROGRAM : ShaderProgram? = null

var BOUND : Shader? = null

var shader : () -> ShaderProgram? = { BOUND?.program ?: GameRenderer.getPositionColorProgram() }

fun resetShader() {
    shader = { BOUND?.program ?: GameRenderer.getPositionColorProgram() }
}

fun setShader(
    program : ShaderProgram?
) {
    shader = { BOUND?.program ?: program }
}

//section shaders

val DUMMY_SHADER = DummyShader("Default")

val GRADIENT_SCREEN_CORE_SHADER = Shader("Gradient", "gradient_screen", mapOf("Position" to VertexFormats.POSITION_ELEMENT, "Color" to VertexFormats.POSITION_ELEMENT))
val GRADIENT_WORLD_CORE_SHADER = Shader("Gradient", "gradient_world", mapOf("Position" to VertexFormats.POSITION_ELEMENT, "Color" to VertexFormats.POSITION_ELEMENT, "Normal" to VertexFormats.NORMAL_ELEMENT))
val GAUSSIAN_BLUR_CORE_SHADER = Shader("Gaussian Blur", "gaussianblur", mapOf("Position" to VertexFormats.POSITION_ELEMENT, "UV" to VertexFormats.UV_ELEMENT, "Color" to VertexFormats.COLOR_ELEMENT), mapOf("weights" to { GaussianBlurRenderer.WEIGHTS!! }))

val GRADIENT_POSTPROCESS_SHADER = PostProcessShader("Gradient", "gradient_postprocess")
val GLOW_GRADIENT_POSTPROCESS_SHADER = PostProcessShader("Glow Gradient", "glow_gradient_postprocess")

val CORE_SHADERS = listOf(
    GRADIENT_SCREEN_CORE_SHADER,
    GRADIENT_WORLD_CORE_SHADER,
    GAUSSIAN_BLUR_CORE_SHADER
)

val POSTPROCESS_SHADERS = listOf(
    GRADIENT_POSTPROCESS_SHADER,
    GLOW_GRADIENT_POSTPROCESS_SHADER
)

//section shader stuff

fun IShader.parseSamplers(
    path : String
) {
    val reader = LavaHack.RESOURCE_FACTORY.openAsReader(Identifier(path))
    val json = JsonHelper.deserialize(reader)!!
    val array = JsonHelper.getArray(json, "samplers", null)!!

    for(sampler in array.iterator()) {
        val jobject = JsonHelper.asObject(sampler, "sampler")
        val name = JsonHelper.getString(jobject, "name")

        samplers.add(name)
    }

    hasPrevSampler = hasPrevSampler || samplers.contains("PrevSampler")
    hasMinecraftSampler = hasMinecraftSampler || samplers.contains("MinecraftSampler")
}

fun IShader.parseUniforms(
    path : String
) {
    val reader = LavaHack.RESOURCE_FACTORY.openAsReader(Identifier(path))
    val json = JsonHelper.deserialize(reader)!!
    val array = JsonHelper.getArray(json, "uniforms", null)!!

    for(uniform in array.iterator()) {
        val `object` = JsonHelper.asObject(uniform, "uniform")
        val name = JsonHelper.getString(`object`, "name")
        val count = JsonHelper.getInt(`object`, "count")
        val values = FloatArray(count)

        val elements = JsonHelper.getArray(`object`, "values")

        for((index, element) in elements.iterator().withIndex()) {
            values[index] = element.asFloat
        }

        val lh_display_name = try { JsonHelper.getString(`object`, "lh_display_name") } catch(_ : Throwable) { "invalid" }
        val lh_type = try { JsonHelper.getString(`object`, "lh_type") } catch(_ : Throwable) { "internal" }
        val lh_number_range = try { JsonHelper.getArray(`object`, "lh_number_range") } catch(_ : Throwable) { JsonArray()  }

        when(lh_type) {
            "animation_speed" -> uniforms[name] = register(SettingNumber("Animation Speed", 0f, 0f..0.7f))

            "int" -> if(count == 1) {
                val value = values[0].toInt()
                val min = lh_number_range[0].asInt
                val max = lh_number_range[1].asInt

                uniforms[name] = register(SettingNumber(name, value, min..max, lh_display_name))
            }

            "float" -> if(count == 1) {
                val value = values[0]
                val min = lh_number_range[0].asFloat
                val max = lh_number_range[1].asFloat

                uniforms[name] = register(SettingNumber(name, value, min..max, lh_display_name))
            }

            "bool" -> {
                val value = values[0].toInt() == 1

                uniforms[name] = register(Setting(name, value, lh_display_name))
            }

            "color" -> if(count == 4) {
                val red = values[0]
                val green = values[1]
                val blue = values[2]
                val alpha = values[3]

                val color = Colour(red, green, blue, alpha)

                uniforms[name] = register(Setting(name, color, lh_display_name))
            }
        }
    }

    prefix(displayName)
}

fun IShader.linkSettings(
    _uniforms : List<GlUniform>
) {
    for (uniform in _uniforms) {
        val name = uniform.name
        val type = uniform.dataType
        val count = uniform.count
        val setting = uniforms[name]

        if(setting != null) {
            if (type <= 7) {
                if (type <= 3) {
                    if (count == 1) {
                        if(setting.value is Int) {
                            (setting as Setting<Int>).onChange = {
                                uniform.set(it.value)
                            }
                        } else {
                            (setting as Setting<Boolean>).onChange = {
                                uniform.set(if(it.value) 1 else 0)
                            }
                        }
                    }
                } else {
                    when (count) {
                        1 -> {
                            (setting as Setting<Float>).onChange = {
                                uniform.set(it.value)
                            }
                        }

                        4 -> {
                            (setting as Setting<Colour>).onChange = {
                                uniform.set(
                                    it.value.red / 255f,
                                    it.value.green / 255f,
                                    it.value.blue / 255f,
                                    it.value.alpha / 255f
                                )
                            }
                        }
                    }
                }
            }

            (setting as Setting<Any>).onChange(setting)
        }
    }
}

//section postprocess
class PostProcessShader(
    override val displayName : String,
    private val shaderName : String
) : IShader {
    override val registry = SettingRegistry()
    override val uniforms = mutableMapOf<String, Setting<*>>()
    override val samplers = mutableListOf<String>()
    //TODO: add usage
    override var hasPrevSampler = false
    override var hasMinecraftSampler = false

    override var created = false

    private var effect : WrappedPostEffectProcessor? = null
    private var framebufferIn : Framebuffer? = null
    private var framebufferOut : Framebuffer? = null

    private var began = false

    override fun parse() {
        val reader = LavaHack.RESOURCE_FACTORY.openAsReader(Identifier("shaders/post/$shaderName.json"))
        val json = JsonHelper.deserialize(reader)!!
        val passes = JsonHelper.getArray(json, "passes", null)!!

        for(pass in passes.iterator()) {
            val `object` = pass.asJsonObject
            val name = JsonHelper.getString(`object`, "name")

            parseUniforms("shaders/program/$name.json")
            parseSamplers("shaders/program/$name.json")
        }
    }

    override fun create() {
        if(!created) {
            framebufferIn = WrappedSimpleFramebuffer()
            framebufferOut = WrappedSimpleFramebuffer()
            effect = WrappedPostEffectProcessor("shaders/post/$shaderName.json")
            effect!!.fakeTarget("bufIn", framebufferIn!!)
            effect!!.fakeTarget("bufOut", framebufferOut!!)

            for(pass in (effect as AccessorPostEffectProcessor).passes) {
                val program = pass.program

                linkSettings((program as AccessorJsonEffectShaderProgram).uniformData)
            }

            created = true
        }
    }

    fun prepareEntity() {
        if(effect is WrappedPostEffectProcessor) {
            effect!!.fakeTarget("bufIn", mc.worldRenderer.entityOutlinesFramebuffer!!)
            effect!!.fakeTarget("bufOut", mc.worldRenderer.entityOutlinesFramebuffer!!)
        }
    }

    override fun begin(
        matrices : MatrixStack
    ) {
        if(effect is WrappedPostEffectProcessor) {
            effect!!.fakeTarget("bufIn", framebufferIn!!)
            effect!!.fakeTarget("bufOut", framebufferOut!!)
        }

//        println("\t\t" + effect!!.getSecondaryTarget("bufIn").toString() + " | " + framebufferIn!!.toString() + " " + mc.worldRenderer.entityOutlinesFramebuffer!!)

//        if(!began) {
            RenderSystem.enableBlend()
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
            RenderSystem.enableCull()
            RenderSystem.enableDepthTest()
            RenderSystem.enablePolygonOffset()
            RenderSystem.polygonOffset(-2f, -2f)

            RenderSystem.setShaderGameTime(System.currentTimeMillis(), mc.tickDelta)

            for(pass in (effect as AccessorPostEffectProcessor).passes) {
                pass.program.getUniformByNameOrDummy("GameTime").set(RenderSystem.getShaderGameTime())
                pass.program.getUniformByNameOrDummy("ModelViewMat").set(RenderSystem.getModelViewMatrix())
            }

            framebufferIn!!.clear(MinecraftClient.IS_SYSTEM_MAC)
            framebufferIn!!.beginWrite(true)

            RenderSystem.disableDepthTest()
            RenderSystem.disableCull()

            began = true
//        }
    }

    override fun end() {
        for(pass in (effect as AccessorPostEffectProcessor).passes) {
            pass.program.bindSampler("PrevSampler") { /*framebufferIn!!.colorAttachment*/effect!!.getSecondaryTarget("bufIn")!!.colorAttachment }
//            pass.program.bindSampler("MinecraftSampler") { mc.framebuffer.colorAttachment }
        }

        RenderSystem.enableCull()
        RenderSystem.enableBlend()

        effect!!.render(0f)
        mc.framebuffer.beginWrite(false)
        began = false
    }

    override fun render() {
        val matrix = RenderSystem.getProjectionMatrix()
        val sorting = RenderSystem.getVertexSorting()

        RenderSystem.enableBlend()
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)

        framebufferOut!!.draw(mc.window!!.framebufferWidth, mc.window!!.framebufferHeight, false)

        RenderSystem.disableBlend()
        RenderSystem.defaultBlendFunc()

        RenderSystem.setProjectionMatrix(matrix, sorting)
        RenderSystem.enableDepthTest()

        RenderSystem.polygonOffset(0f, 0f)
        RenderSystem.disablePolygonOffset()
        RenderSystem.enableDepthTest()
    }

    fun renderEffect() {
        for(pass in (effect as AccessorPostEffectProcessor).passes) {
            pass.program.bindSampler("PrevSampler") { framebufferIn!!.colorAttachment }
//            pass.program.bindSampler("MinecraftSampler") { mc.framebuffer.colorAttachment }
        }

        effect!!.render(0f)
    }
}

//section shader
class Shader(
    override val displayName : String,
    private val fileName : String,
    private val elements : Map<String, VertexFormatElement>,
    private val externalUniforms : Map<String, () -> Any> = emptyMap()
) : IShader {
    override val registry = SettingRegistry()
    override val uniforms = mutableMapOf<String, Setting<*>>()
    override val samplers = mutableListOf<String>()
    override var created = false
    override var hasPrevSampler = false
    override var hasMinecraftSampler = false

    var program : ShaderProgram? = null

    private val externalGlUniforms = mutableMapOf<GlUniform, () -> Any>()

    override fun parse() {
        parseUniforms("shaders/core/$fileName.json")
        parseSamplers("shaders/core/$fileName.json")
    }

    override fun create() {
        if(!created) {
            program = programOf(fileName, elements, this)

            if(program != null) {
                linkSettings((program as AccessorShaderProgram).uniforms!!)

                for((name, value) in externalUniforms) {
                    val glUniform = program!!.getUniform(name)

                    if(glUniform == null) {
                        LavaHack.LOGGER.error("Cannot find $name uniform in $fileName core shader")
                    } else {
                        externalGlUniforms[glUniform] = value
                    }
                }

                created = true
            }
        }
    }

    override fun bind() {
        BOUND = this
    }

    override fun unbind() {
        BOUND = null
    }

    override fun begin() {
        bind()

        RenderSystem.setShaderGameTime(System.currentTimeMillis(), mc.tickDelta)
    }

    override fun end() {
        unbind()
    }

    fun defaultSamplers() {
        if(hasMinecraftSampler) {
            println("writing mc sampler")

            mc.framebuffer.endWrite()
            mc.framebuffer.beginRead()

            program?.addSampler("MinecraftSampler", mc.framebuffer.colorAttachment)

            mc.framebuffer.endRead()
            mc.framebuffer.beginWrite(true)
        }
    }

    fun externalUniforms() {
        for(entry in externalGlUniforms) {
            val glUniform = entry.key

            println(glUniform.name)
            println(entry.value().javaClass.simpleName)

            when(
                val value = entry.value()
            ) {
                is FloatArray -> glUniform.set(value)
                is Boolean -> glUniform.set(if(value) 1 else 0)
            }
        }
    }
}

//section default classes

class DummyShader(
    override val displayName : String
) : IShader {
    override val registry = SettingRegistry()
    override val uniforms = mutableMapOf<String, Setting<*>>()
    override val samplers = mutableListOf<String>()
    override var created = true
    override var hasPrevSampler = false
    override var hasMinecraftSampler = false

    override fun parse() { }

    override fun create() { }
}

class DummyShaderProgram : ShaderProgram(
    LavaHack.RESOURCE_FACTORY,
    "dummy",
    VertexFormat(ImmutableMap.builder<String, VertexFormatElement>().putAll(mapOf("position" to VertexFormats.POSITION_ELEMENT, "color" to VertexFormats.COLOR_ELEMENT)).build())
) {
    override fun bind() { }

    override fun unbind() { }
}

open class WrappedSimpleFramebuffer : SimpleFramebuffer(
    mc.window.framebufferWidth,
    mc.window.framebufferHeight,
    false,
    MinecraftClient.IS_SYSTEM_MAC
) {
    init {
        listener<WindowEvent.Resize> {
            resize(mc.window.framebufferWidth, mc.window.framebufferHeight, MinecraftClient.IS_SYSTEM_MAC)
        }
    }
}

class WrappedPostEffectProcessor(
    path : String
) : PostEffectProcessor(
    TextureManager(LavaHack.RESOURCE_MANAGER),
    LavaHack.RESOURCE_MANAGER,
    mc.framebuffer,
    Identifier(path)
) {
    init {
        setupDimensions(mc.window.framebufferWidth, mc.window.framebufferHeight)

        listener<WindowEvent.Resize> {
            setupDimensions(mc.window.framebufferWidth, mc.window.framebufferHeight)
        }
    }

    fun fakeTarget(
        name : String,
        framebuffer : Framebuffer
    ) {
        (this as IPostEffectProcessor).addFakeTarget(name, framebuffer)
    }
}