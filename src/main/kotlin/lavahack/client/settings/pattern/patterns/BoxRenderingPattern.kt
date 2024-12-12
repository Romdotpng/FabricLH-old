package lavahack.client.settings.pattern.patterns

import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.Pattern
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Colour
import lavahack.client.utils.box
import lavahack.client.utils.client.enums.ColorerModes
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.IBoxColorer
import lavahack.client.utils.client.interfaces.impl.BoxColorer
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.render.world.arrow
import lavahack.client.utils.render.world.correct
import lavahack.client.utils.render.world.full
import lavahack.client.utils.render.world.tracer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

/**
 * @author _kisman_
 * @since 10:26 of 26.05.2023
 */
@Suppress("LocalVariableName", "PrivatePropertyName")
open class BoxRenderingPattern(
    private val tracer : Boolean = false,
    private val arrow : Boolean = false
) : Pattern() {
    private val FILL = register(Setting("Fill", true))
    private val OUTLINE = register(Setting("Outline", true))
    private val TRACER = register(Setting("Tracer", false).visible { tracer })
    private val ARROW = register(Setting("Arrow", false).visible { arrow })

    private val FILL_MODE = register(SettingEnum("Fill Mode", ColorerModes.Single))
    private val OUTLINE_MODE = register(SettingEnum("Outline Mode", ColorerModes.Single))

    //TODO: add postprocess shaders
    private val SHADER_GROUP = register(SettingGroup("Shader"))
    private val SHADER = register(SHADER_GROUP.add(SettingEnum("Shader", CoreShaders.None)))
    private val SHADER_SECOND_LAYER = register(SHADER_GROUP.add(Setting("Second Layer", false, "2nd Layer")))

    private val WIDTHS_GROUP = register(SettingGroup("Widths"))
    private val OUTLINE_WIDTH = register(WIDTHS_GROUP.add(SettingNumber("Outline", 1.0, 0.1..5.0)))
    private val TRACER_WIDTH = register(WIDTHS_GROUP.add(SettingNumber("Tracer", 1f, 0.1f..5f).visible { tracer }))
    private val ARROW_WIDTH = register(WIDTHS_GROUP.add(SettingNumber("Arrow", 1f, 0.1f..5f).visible { arrow }))

    private val DEPTH = register(Setting("Depth", false))

    private val SCALE_GROUP = register(SettingGroup("Scale"))
    private val SCALE_STATE = register(SCALE_GROUP.add(Setting("Scale State", false, "State")))
    private val SCALE_VALUE = register(SCALE_GROUP.add(SettingNumber("Scale Value", 0.002, 0.002..0.2, "Offset")))

    private val ARROW_RANGE = register(SettingNumber("Arrow Range", 10.0, 0.0..50.0).visible { arrow })

    private val COLORS_GROUP = register(SettingGroup("Colors"))

    private val FILL_COLORS_GROUP = register(COLORS_GROUP.add(SettingGroup("Fill")))
    private val FILL_COLORER = register(FILL_COLORS_GROUP.add(BoxColorer().also { it.prefix("Fill") }))

    private val OUTLINE_COLORS_GROUP = register(COLORS_GROUP.add(SettingGroup("Outline")))
    private val OUTLINE_COLORER = register(OUTLINE_COLORS_GROUP.add(BoxColorer().also { it.prefix("Outline") }))

    private val TRACER_COLORS_GROUP = register(COLORS_GROUP.add(SettingGroup("Tracer").visible { tracer }))
    private val TRACER_COLOR = register(TRACER_COLORS_GROUP.add(Setting("Color", Colour(-1))))

    private val ARROW_COLORS_GROUP = register(COLORS_GROUP.add(SettingGroup("Arrow").visible { arrow }))
    private val ARROW_INSIDE_COLOR = register(ARROW_COLORS_GROUP.add(Setting("Inside Color", Colour(-1).alpha(120))))
    private val ARROW_OUTSIDE_COLOR = register(ARROW_COLORS_GROUP.add(Setting("Outside Color", Colour(-1))))

    val fillColorer = BoxColorer()
    val outlineColorer = BoxColorer()

    init {
        SHADER_GROUP.prefix("Shader")
        WIDTHS_GROUP.prefix("Width")
        SCALE_GROUP.prefix("Scale")
        TRACER_COLORS_GROUP.prefix("Tracer Color")
        ARROW_COLORS_GROUP.prefix("Arrow Color")
    }

    fun willDraw() = FILL.value || OUTLINE.value || TRACER.value

    fun draw(
        matrices : MatrixStack,
        box : Box,
        update : Boolean = true
    ) {
        if(update) {
            updateColorers()
        }

        //TODO: custom shader states settings for tracer and arrow
        val task = {
            full(
                matrices,
                box.correct(),
                if(FILL.value) fillColorer else null,
                if(OUTLINE.value) outlineColorer else null,
                OUTLINE_WIDTH.value
            )

            if(TRACER.value) {
                tracer(
                    matrices,
                    box.center,
                    TRACER_COLOR.value,
                    TRACER_WIDTH.value
                )
            }

            if(ARROW.value) {
                arrow(
                    matrices,
                    box.center,
                    ARROW_INSIDE_COLOR.value,
                    ARROW_OUTSIDE_COLOR.value,
                    ARROW_WIDTH.value,
                    ARROW_RANGE.value
                )
            }
        }

        if(SHADER.valEnum == CoreShaders.None || SHADER_SECOND_LAYER.value) {
            task()
        }

        if(SHADER.valEnum != CoreShaders.None) {
            SHADER.valEnum.worldShader.begin()
            //TODO: optimize drawing it twice by drawing only vertex consumer
            task()
            SHADER.valEnum.worldShader.end()
        }
    }

    fun draw(
        matrices : MatrixStack,
        pos : BlockPos
    ) {
        val box = pos.box()

        draw(
            matrices,
            box
        )
    }

    fun updateColorers() {
        updateColorers(FILL_COLORER, OUTLINE_COLORER)
    }

    fun updateColorers(
        _fillColorer : IBoxColorer,
        _outlineColorer : IBoxColorer
    ) {
        updateColorer(FILL_MODE.value.current, _fillColorer, fillColorer)
        updateColorer(OUTLINE_MODE.value.current, _outlineColorer, outlineColorer)
    }

    private fun updateColorer(
        mode : ColorerModes,
        from : IBoxColorer,
        to : IBoxColorer
    ) {
        when(mode) {
            ColorerModes.Single -> {
                to.color1.value = from.color1.value
                to.color2.value = from.color1.value
                to.color3.value = from.color1.value
                to.color4.value = from.color1.value
                to.color5.value = from.color1.value
                to.color6.value = from.color1.value
                to.color7.value = from.color1.value
                to.color8.value = from.color1.value
            }
            ColorerModes.Double -> {
                to.color1.value = from.color2.value
                to.color2.value = from.color2.value
                to.color3.value = from.color2.value
                to.color4.value = from.color2.value
                to.color5.value = from.color1.value
                to.color6.value = from.color1.value
                to.color7.value = from.color1.value
                to.color8.value = from.color1.value
            }
            ColorerModes.Chroma -> {
                to.color1.value = from.color1.value
                to.color2.value = from.color2.value
                to.color3.value = from.color3.value
                to.color4.value = from.color4.value
                to.color5.value = from.color5.value
                to.color6.value = from.color6.value
                to.color7.value = from.color7.value
                to.color8.value = from.color8.value
            }
        }
    }
}

