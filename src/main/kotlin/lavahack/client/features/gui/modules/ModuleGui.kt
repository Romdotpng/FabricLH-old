@file:Suppress("UNCHECKED_CAST")

package lavahack.client.features.gui.modules

import com.mojang.blaze3d.systems.RenderSystem
import lavahack.client.features.gui.LavaHackScreen
import lavahack.client.features.gui.huds.components.HudComponent
import lavahack.client.features.gui.modules.component.*
import lavahack.client.features.gui.modules.component.components.*
import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.InputController
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.settings.types.combo.Element
import lavahack.client.utils.*
import lavahack.client.utils.client.enums.BindTypes
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.*
import lavahack.client.utils.client.interfaces.impl.AnimatorContext
import lavahack.client.utils.client.interfaces.impl.Binder
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.math.sqrt
import lavahack.client.utils.render.screen.*
import lavahack.client.utils.render.shader.PostProcessShader
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import org.joml.Vector2d
import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.ranges.coerceIn

/**
 * @author _kisman_
 * @since 18:24 of 09.05.2023
 */
@Suppress("LocalVariableName", "MemberVisibilityCanBePrivate")
object ModuleGui : LavaHackScreen(
    "Modules",
    true
) {
    private val COLOR_GROUP = register(SettingGroup("Colors"))
    val PRIMARY_COLOR = register(COLOR_GROUP.add(Setting("Primary Color", Colour(255, 0, 0, 255), "Primary")))
    val BACKGROUND_COLOR = register(COLOR_GROUP.add(Setting("Background Color", Colour(0, 0, 0, 120), "Background")))
    val BACKGROUND_COLOR2 = register(COLOR_GROUP.add(Setting("Background Color 2", Colour(0, 0, 0, 120), "2nd Background")))
    val OUTLINE_COLOR = register(COLOR_GROUP.add(Setting("Outline Color", Colour(0, 0, 0, 255), "Outline")))
    val DESCRIPTION_COLOR = register(COLOR_GROUP.add(Setting("Description Color", Colour(0, 0, 0, 120), "Description")))
    val HOVER_COLOR = register(COLOR_GROUP.add(Setting("Hover Color", Colour(255, 255, 255, 60), "Hovering")))
    val PRIMARY_TEXT_COLOR = register(COLOR_GROUP.add(Setting("Primary Text Color", Colour(-1), "Primary Text")))
    val BACKGROUND_TEXT_COLOR = register(COLOR_GROUP.add(Setting("Background Text Color", Colour(-1), "Background Text")))
    val OVERRIDE_HEADER_ALPHA = register(COLOR_GROUP.add(Setting("Override Header Alpha", true)))
    val OVERRIDE_LINES_ALPHA = register(COLOR_GROUP.add(Setting("Override Lines Alpha", true)))

    val BACKGROUND1 = register(Setting("Background", true))
    val BACKGROUND2 = register(Setting("Background2", true))
    val SHADERED_BACKGROUNDS = register(Setting("Shadered Backgrounds", false))
    val DESCRIPTION = register(Setting("Description", true))

    private val LINES_GROUP = register(SettingGroup("Lines"))
    val VERTICAL_LINES = register(LINES_GROUP.add(Setting("Vertical Lines", false)))
    val HORIZONTAL_LINES = register(LINES_GROUP.add(Setting("Horizontal Lines", false)))

    private val OUTLINE_GROUP = register(SettingGroup("Outline"))
    private val HIGHLIGHT_OUTLINE = register(OUTLINE_GROUP.add(Setting("Highlight Outline", false)))
    val VERTICAL_OUTLINE = register(OUTLINE_GROUP.add(Setting("Vertical Outline", false)))
    private val LINE_WIDTH = register(OUTLINE_GROUP.add(SettingNumber("Line Width", 1.0, 0.01..1.0)))

    val TEXT_OFFSET_X = register(SettingNumber("Text Offset X", 5, 0..5))
    val LAYER_STEP_OFFSET = register(SettingNumber("Layer Step Offset", 5, 0..5))
    val HEIGHT = register(SettingNumber("Height", 13, 10..20))

    val OFFSETS_X = register(SettingNumber("Offsets X", 0.0, 0.0..2.0))
    val OFFSETS_Y = register(SettingNumber("Offsets Y", 0.0, 0.0..2.0))
    val SCALE = register(SettingNumber("Scale", 1.0, 0.5..2.0))

    private val SCROLL_GROUP = register(SettingGroup("Scroll"))
    private val SCROLL_V_SPEED = register(SCROLL_GROUP.add(SettingNumber("Vertical", 100, 0..1000)))
    private val SCROLL_H_SPEED = register(SCROLL_GROUP.add(SettingNumber("Horizontal", 100, 0..1000)))
    private val SCROLL_H_KEY = register(SCROLL_GROUP.add(Setting("Vertical Key", Binder("^ Key", BindTypes.Keyboard, GLFW.GLFW_KEY_LEFT_SHIFT, -1, false))))

    private val ANIMATIONS_GROUP = register(SettingGroup("Animations"))
    val TOGGLE_ANIMATION_GROUP = register(ANIMATIONS_GROUP.add(SettingGroup("Toggle")))
    val TOGGLE_ANIMATION_STATE = register(TOGGLE_ANIMATION_GROUP.add(Setting("State", false)))
    val TOGGLE_ANIMATION_EASING = register(TOGGLE_ANIMATION_GROUP.add(SettingEnum("Easing", Easings.Linear)))
    val TOGGLE_ANIMATION_LENGTH = register(TOGGLE_ANIMATION_GROUP.add(SettingNumber("Length", 750L, 0L..1000L)))
    val TOGGLE_ANIMATION_CIRCLE = register(TOGGLE_ANIMATION_GROUP.add(Setting("Circle", false)))
    val TOGGLE_ANIMATION_FROM_L_TO_R = register(TOGGLE_ANIMATION_GROUP.add(Setting("From Left To Right", true, "Left -> Right")))
    val TOGGLE_ANIMATION_FROM_R_TO_L = register(TOGGLE_ANIMATION_GROUP.add(Setting("From Right To Left", true, "Right -> Left")))
    val TOGGLE_ANIMATION_ALPHA = register(TOGGLE_ANIMATION_GROUP.add(Setting("Alpha", false)))
    private val SLIDER_ANIMATION_GROUP = register(ANIMATIONS_GROUP.add(SettingGroup("Slider")))
    val SLIDER_ANIMATION_STATE = register(SLIDER_ANIMATION_GROUP.add(Setting("State", false)))
    val SLIDER_ANIMATION_SPEED = register(SLIDER_ANIMATION_GROUP.add(SettingNumber("Speed", 0.05, 0.01..1.0)))
    private val FRAME_ANIMATION_GROUP = register(ANIMATIONS_GROUP.add(SettingGroup("Frame")))
    val FRAME_ANIMATION_STATE = register(FRAME_ANIMATION_GROUP.add(Setting("State", false)))
    val FRAME_ANIMATION_SPEED = register(FRAME_ANIMATION_GROUP.add(SettingNumber("Speed", 0.05, 0.01..1.0)))
    private val CONTAINER_ANIMATION_GROUP = register(ANIMATIONS_GROUP.add(SettingGroup("Container")))
    val CONTAINER_ANIMATION_STATE = register(CONTAINER_ANIMATION_GROUP.add(Setting("State", false)))
    val CONTAINER_ANIMATION_EASING = register(CONTAINER_ANIMATION_GROUP.add(SettingEnum("Easing", Easings.Linear)))
    val CONTAINER_ANIMATION_LENGTH = register(CONTAINER_ANIMATION_GROUP.add(SettingNumber("Length", 750L, 0L..1000L)))

    //TODO: add custom shaders for different things
    private val SHADERS_GROUP = register(SettingGroup("Shaders"))
    private val SHADER = register(SHADERS_GROUP.add(SettingEnum("Shader", CoreShaders.None)))

    private val BLUR_GROUP = /*register*/(SettingGroup("Blur"))
    private val BLUR_STATE = register(BLUR_GROUP.add(Setting("State", false)))
    private val BLUR_SHADER = register(BLUR_GROUP.add(PostProcessShader("Blur", "blur_postprocess")))

    val TOGGLE_ANIMATOR = AnimatorContext(TOGGLE_ANIMATION_EASING, TOGGLE_ANIMATION_LENGTH)
    val CONTAINER_ANIMATOR = AnimatorContext(CONTAINER_ANIMATION_EASING, CONTAINER_ANIMATION_LENGTH)

    const val WIDTH = 120

    private var blurRenderThing = { }
    private var normalRenderThing1 = { }
    private var normalRenderThing2 = { }
    private var preRenderThing = { }
    private var shaderableThing1 = { }
    private var shaderableThing2 = { }
    private var postRenderThing1 = { }
    private var postRenderThing2 = { }

    var frames = mutableListOf<Frame>()

    val MODULE_FRAMES = mutableListOf<Frame>()

    init {
        ANIMATIONS_GROUP.prefix("Animations")
        TOGGLE_ANIMATION_GROUP.prefix("Toggle")
        SLIDER_ANIMATION_GROUP.prefix("Slider")
        FRAME_ANIMATION_GROUP.prefix("Frame")
        CONTAINER_ANIMATION_GROUP.prefix("Container")
        BLUR_GROUP.prefix("Blur")
    }

    fun create() {
        var offsetX = 5.0

        for(category in Module.Category.values()) {
            MODULE_FRAMES.add(Frame(category.modules, category.display, offsetX.also { offsetX += WIDTH + 1.0 }, 25.0))
        }
    }

    override fun onRender(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) {
        blurRenderThing = { }
        normalRenderThing1 = { }
        normalRenderThing2 = { }
        preRenderThing = { }
        shaderableThing1 = { }
        shaderableThing2 = { }
        postRenderThing1 = { }
        postRenderThing2 = { }

        for(frame in frames) {
            frame.refresh()
            frame.handleRender(context, mouseX, mouseY)
        }

        if(BLUR_STATE.value) {
            BLUR_SHADER.begin(context.matrices)

            blurRenderThing()

            BLUR_SHADER.end()
            BLUR_SHADER.render()
        }

        normalRenderThing1()
        normalRenderThing2()
        SHADER.drawScreen(preRenderThing)
        SHADER.drawScreen(shaderableThing1)
        postRenderThing1()
        postRenderThing2()
        SHADER.drawScreen(shaderableThing2)
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) = true.also {
        for(frame in frames) {
            frame.mouseClicked(mouseX, mouseY, button)
        }
    }

    override fun mouseReleased(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) = true.also {
        for(frame in frames) {
            frame.mouseReleased(mouseX, mouseY, button)
        }
    }

    override fun mouseDragged(
        mouseX : Double,
        mouseY : Double,
        button : Int,
        deltaX : Double,
        deltaY : Double
    ) = true.also {
        for(frame in frames) {
            frame.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }
    }

    override fun keyPressed(
        keyCode : Int,
        scanCode : Int,
        modifiers : Int
    ) = true.also {
        for(frame in frames) {
            frame.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    override fun charTyped(
        char : Char,
        modifiers : Int
    ) = true.also {
        for(frame in frames) {
            frame.charTyped(char, modifiers)
        }
    }

    override fun mouseScrolled(
        mouseX : Double,
        mouseY : Double,
        amount : Double
    ) = super.mouseScrolled(mouseX, mouseY, amount).also {
        val horizontal = if(InputController.pressedKey(SCROLL_H_KEY.value.keyboardKey, true)) 1 else 0
        val coeff = if(amount > 0.0) {
            1
        } else {
            -1
        }

        for(frame in frames) {
            frame.update((frame.endX ?: frame.context.x) + horizontal * coeff * SCROLL_H_SPEED.value, (frame.endY ?: frame.context.y) + (1 - horizontal) * coeff * SCROLL_V_SPEED.value)
        }
    }

    override fun onOpen() {
        frames = MODULE_FRAMES
    }

    fun modifyWidth(
        step : Int,
        width : Int
    ) = width - step * LAYER_STEP_OFFSET.value * 2

    fun layerOffset(
        component : IComponent
    ) = layerOffset(component.context.layer)

    fun layerOffset(
        layer : Int
    ) = layer * LAYER_STEP_OFFSET.value

    fun lines(
        context : DrawContext,
        component : IComponent
    ) {
        val color = if(OVERRIDE_LINES_ALPHA.value) Colour(PRIMARY_COLOR.value.rgb).alpha(255) else PRIMARY_COLOR.value

        if(VERTICAL_LINES.value) {
            addShaderRender {
                rectWH(
                    context,
                    component.context.x + layerOffset(component),
                    component.context.y + component.context.offset,
                    1,
                    component.height.first,
                    color
                )

                rectWH(
                    context,
                    component.context.x + layerOffset(component) + component.width - 1,
                    component.context.y + component.context.offset,
                    1,
                    component.height.first,
                    color
                )
            }
        }

        if(HORIZONTAL_LINES.value && component is IContainable && component.open && component.components.isNotEmpty()) {
            //TODO: optimize
            val data = collectData(component)
            val height = component.fullHeight + component.height.first

            if(height != component.height.first) {
                if(component is Frame) {
                    addShaderRender {
                        rectWH(
                            context,
                            component.context.x + layerOffset(max(data.second - 1, 0)),
                            component.context.y + component.context.offset + height,
                            1,
                            OFFSETS_Y.value,
                            color
                        )

                        rectWH(
                            context,
                            component.context.x + modifyWidth(max(data.second - 1, 0), WIDTH) + layerOffset(max(data.second - 1, 0)),
                            component.context.y + component.context.offset + height,
                            -1,
                            OFFSETS_Y.value,
                            color
                        )

                        rectWH(
                            context,
                            component.context.x + layerOffset(max(data.second - 1, 0)),
                            component.context.y + component.context.offset + height + OFFSETS_Y.value,
                            modifyWidth(max(data.second - 1, 0), WIDTH),
                            1,
                            color
                        )
                    }

                    if(BACKGROUND1.value) {
                        addPreRender {
                            rectWH(
                                context,
                                component.context.x + layerOffset(max(data.second - 1, 0)) + LINE_WIDTH.value,
                                component.context.y + component.context.offset + height,
                                modifyWidth(max(data.second - 1, 0), WIDTH) - LINE_WIDTH.value * 2,
                                OFFSETS_Y.value,
                                BACKGROUND_COLOR.value
                            )
                        }
                    }
                } else {
                    val width = if(VERTICAL_LINES.value) 1 else 2

                    addShaderRender {
                        rectWH(
                            context,
                            component.context.x + layerOffset(component),
                            component.context.y + component.context.offset + component.height.first,
                            width,
                            1,
                            color
                        )

                        rectWH(
                            context,
                            component.context.x + component.width + layerOffset(component),
                            component.context.y + component.context.offset + component.height.first,
                            -width,
                            1,
                            color
                        )

                        rectWH(
                            context,
                            component.context.x + layerOffset(max(data.second - 1, 0)),
                            component.context.y + component.context.offset + data.first,
                            width,
                            -1,
                            color
                        )

                        rectWH(
                            context,
                            component.context.x + modifyWidth(max(data.second - 1, 0), WIDTH) + layerOffset(max(data.second - 1, 0)),
                            component.context.y + component.context.offset + data.first,
                            -width,
                            -1,
                            color
                        )
                    }
                }
            }
        }
    }

    fun outline(
        context : DrawContext,
        component : IComponent
    ) {
        if(HIGHLIGHT_OUTLINE.value) {
            addShaderRender2 {
                rectWH(
                    context,
                    component.context.x + layerOffset(component) + OFFSETS_X.value,
                    component.context.y + component.context.offset + OFFSETS_Y.value,
                    component.width - OFFSETS_X.value * 2,
                    LINE_WIDTH.value,
                    OUTLINE_COLOR.value
                )

                rectWH(
                    context,
                    component.context.x + layerOffset(component) + OFFSETS_X.value,
                    component.context.y + component.context.offset + OFFSETS_Y.value,
                    LINE_WIDTH.value,
                    component.height.first - OFFSETS_Y.value * 2,
                    OUTLINE_COLOR.value
                )

                rectWH(
                    context,
                    component.context.x + layerOffset(component) + OFFSETS_X.value,
                    component.context.y + component.context.offset + component.height.first - OFFSETS_Y.value - LINE_WIDTH.value,
                    component.width - OFFSETS_X.value * 2,
                    LINE_WIDTH.value,
                    OUTLINE_COLOR.value
                )

                rectWH(
                    context,
                    component.context.x + component.width + layerOffset(component) - OFFSETS_X.value - LINE_WIDTH.value,
                    component.context.y + component.context.offset + OFFSETS_Y.value,
                    LINE_WIDTH.value,
                    component.height.first - OFFSETS_Y.value * 2,
                    OUTLINE_COLOR.value
                )
            }
        }

        if(VERTICAL_OUTLINE.value) {
            addShaderRender2 {
                rectWH(
                    context,
                    component.context.x + layerOffset(component),
                    component.context.y + component.context.offset,
                    LINE_WIDTH.value,
                    component.height.first,
                    OUTLINE_COLOR.value
                )

                rectWH(
                    context,
                    component.context.x + component.width + layerOffset(component) - LINE_WIDTH.value,
                    component.context.y + component.context.offset,
                    LINE_WIDTH.value,
                    component.height.first,
                    OUTLINE_COLOR.value
                )
            }
        }
    }

    fun backgrounds(
        context : DrawContext,
        component : IComponent,
        mouseX : Int,
        mouseY : Int
    ) {
        val lambda = {
            background(context, component)
            background2(context, component)
        }

        if(SHADERED_BACKGROUNDS.value) {
            addPreRender(lambda)
        } else {
            lambda()
        }

        if (component.hovering(mouseX.toDouble(), mouseY.toDouble()) && component !is ModuleComponent) {
            rect(context, component, Color(255, 255, 255, 60), true)
        }
    }

    fun background(
        context : DrawContext,
        component : IComponent
    ) {
        if(BACKGROUND1.value) {
            rect(
                context,
                component,
                BACKGROUND_COLOR.value,
                false,
                defaultOffsetX = if(VERTICAL_LINES.value) 1.0 else 0.0
            )
        }
    }

    fun fill(
        context : DrawContext,
        component : IComponent,
        offsets : Boolean,
        defaultOffsetX : Double = 0.0,
        defaultTopOffsetY : Double = 0.0,
        defaultBottomOffsetY : Double = 0.0
    ) {
        addShaderRender {
            rect(
                context,
                component,
                if(component is Frame && OVERRIDE_HEADER_ALPHA.value) Colour(PRIMARY_COLOR.value.rgb).alpha(255) else PRIMARY_COLOR.value,
                offsets,
                defaultOffsetX,
                defaultTopOffsetY,
                defaultBottomOffsetY
            )
        }
    }

    fun background2(
        context : DrawContext,
        component : IComponent
    ) {
        if(BACKGROUND2.value && component is ModuleComponent && !component.stateSupplier()) {
            rect(context, component, BACKGROUND_COLOR2.value, true)
        }
    }

    fun rect(
        context : DrawContext,
        component : IComponent,
        color : Color,
        offsets : Boolean,
        defaultOffsetX : Double = 0.0,
        defaultTopOffsetY : Double = 0.0,
        defaultBottomOffsetY : Double = 0.0
    ) {
        val offsetX = if(offsets) {
            OFFSETS_X.value
        } else {
            defaultOffsetX
        }

        val topOffsetY = if(offsets) {
            OFFSETS_Y.value
        } else {
            defaultTopOffsetY
        }

        val bottomOffsetY = if(offsets) {
            OFFSETS_Y.value
        } else {
            defaultBottomOffsetY
        }

        rectWH(
            context,
            component.context.x + layerOffset(component) + offsetX,
            component.context.y + component.context.offset + topOffsetY,
            (if(offsets) component.width() else component.width) - offsetX * 2,
            component.height.second - topOffsetY - bottomOffsetY,
            color
        )
    }

    fun outline(
        context : DrawContext,
        component : IComponent,
        color : Color,
        width : Number,
        offsets : Boolean
    ) {
        val offsetX = if(offsets) {
            OFFSETS_X.value
        } else {
            0.0
        }

        val offsetY = if(offsets) {
            OFFSETS_Y.value
        } else {
            0.0
        }

        outlineRectWH(
            context,
            component.context.x + layerOffset(component) + offsetX,
            component.context.y + component.context.offset + offsetY,
            (if(offsets) component.width() else component.width) - offsetX * 2,
            component.height.first - offsetY * 2,
            color,
            width
        )
    }

    fun gradientRect(
        context : DrawContext,
        component : IComponent,
        color1 : Color,
        color2 : Color,
        color3 : Color,
        color4 : Color,
        offsets : Boolean
    ) {
        val offsetX = if(offsets) {
            OFFSETS_X.value
        } else {
            0.0
        }

        val offsetY = if(offsets) {
            OFFSETS_Y.value
        } else {
            0.0
        }

        gradientRectWH(
            context,
            component.context.x + layerOffset(component) + offsetX,
            component.context.y + component.context.offset + offsetY,
            component.width - offsetX * 2,
            component.height.first - offsetY * 2,
            color1,
            color2,
            color3,
            color4
        )
    }

    fun ToggleableComponent.animatedRect(
        context : DrawContext,
        _color : Color = PRIMARY_COLOR.value,
        offsets : Boolean
    ) {
        if(TOGGLE_ANIMATION_STATE.value) {
            val offsetX = if(offsets) {
                OFFSETS_X.value
            } else {
                0.0
            }

            val offsetY = if(offsets) {
                OFFSETS_Y.value
            } else {
                0.0
            }

            val x = this.context.x + layerOffset(this) + offsetX
            val y = this.context.y + this.context.offset + offsetY
            val width = (if(offsets) width() else width) - offsetX * 2
            val height = height.first - offsetY * 2
            val start = animator.ENABLE_ANIMATOR.get()
            val end = animator.ENABLE_ANIMATOR2.get()
            var color = _color

            if(TOGGLE_ANIMATION_ALPHA.value) {
                color = Color(color.red, color.green, color.blue, (color.alpha * start).coerceIn(0.0..255.0).toInt())
            }

            if(TOGGLE_ANIMATION_CIRCLE.value) {
                addShaderRender {
                    val centerVec = Vector2d(mouseX.toDouble(), mouseY.toDouble())
                    val vec1 = Vector2d(x, y)
                    val vec2 = Vector2d(x, y + height)
                    val vec3 = Vector2d(x + width, y)
                    val vec4 = Vector2d(x + width, y + height)

                    val distance1Sq = centerVec distanceSq vec1
                    val distance2Sq = centerVec distanceSq vec2
                    val distance3Sq = centerVec distanceSq vec3
                    val distance4Sq = centerVec distanceSq vec4

                    val maxDistanceSq = max(max(max(distance1Sq, distance2Sq), distance3Sq), distance4Sq)
                    val maxDistance = sqrt(maxDistanceSq)

                    var start1 = start
                    var end1 = end

                    if(!stateSupplier()) {
                        if(start == 1.0 && end == 1.0) {
                            start1 = 0.0
                            end1 = 0.0
                        }
                    }

                    val radius1 = maxDistance * max(start1, end1)
                    val radius2 = maxDistance * min(start1, end1)

                    circleRectWH(
                        context,
                        x,
                        y,
                        width,
                        height,
                        mouseX,
                        mouseY,
                        radius1,
                        radius2,
                        color
                    )
                }
            }

            if(TOGGLE_ANIMATION_FROM_L_TO_R.value) {
                addShaderRender {
                    rect(
                        context,
                        x + width * start,
                        y,
                        x + width * end,
                        y + height,
                        color
                    )
                }
            }

            if(TOGGLE_ANIMATION_FROM_R_TO_L.value) {
                addShaderRender {
                    rect(
                        context,
                        x + width * start,
                        y,
                        x + width * end,
                        y + height,
                        color
                    )
                }
            }

            if(!TOGGLE_ANIMATION_FROM_L_TO_R.value && !TOGGLE_ANIMATION_FROM_R_TO_L.value && !TOGGLE_ANIMATION_CIRCLE.value) {
                addShaderRender {
                    rect(
                        context,
                        this,
                        color,
                        offsets
                    )
                }
            }

            animator.update(stateSupplier())
        } else {
            if(stateSupplier()) {
                addShaderRender {
                    rect(
                        context,
                        this,
                        _color,
                        offsets
                    )
                }
            }
        }
    }

    fun IComponent.blurredRect(
        context : DrawContext,
        offsets : Boolean
    ) {
        if(BLUR_STATE.value) {
            addBlurRender {
                rect(context, this, Color(-1), offsets)
            }
        }
    }

    fun drawStringWithShadow(
        context : DrawContext,
        text : String,
        component : IComponent,
        left : Boolean = true,
        primary : Boolean = false,
    ) {
        addPostRender {
            drawString(
                context,
                text,
                if(left) component.context.x + layerOffset(component) + TEXT_OFFSET_X.value
                else component.context.x + layerOffset(component) + component.width - TEXT_OFFSET_X.value - stringWidth(text),
                component.context.y + component.context.offset + HEIGHT.value / 2 - fontHeight() / 2 + HEIGHT.value % 2,
                if(component is Frame) Colour(-1) else if(primary) PRIMARY_TEXT_COLOR.value else BACKGROUND_TEXT_COLOR.value,
                true
            )
        }
    }

    fun drawSuffix(
        context : DrawContext,
        text : String,
        suffix : String,
        component : IComponent,
        step : Int
    ) {
        context.matrices.push()
        context.matrices.scale(0.5f, 0.5f, 1.0f)

        drawString(
            context,
            suffix,
            (component.context.x + layerOffset(component) + TEXT_OFFSET_X.value + stringWidth(text)) * 2f,
            (component.context.y + component.context.offset + when(step) {
                1 -> 0
                3 -> component.height.first - mc.textRenderer.fontHeight / 2
                else -> throw UnsupportedOperationException("Step $step is unsupported step for gui suffix")
            }) * 2f,
            Color.WHITE,
            true
        )

        context.matrices.pop()
    }

    private fun prepareRenderBlock(
        block : () -> Unit
    ) : () -> Unit {
        val context = scissorContext

        return {
            scissorContext = context

            block()

            scissorContext = null
        }
    }

    fun addBlurRender(
        block : () -> Unit
    ) {
        blurRenderThing = compare(blurRenderThing, prepareRenderBlock(block))
    }

    fun addNormalRender(
        block : () -> Unit
    ) {
        normalRenderThing1 = compare(normalRenderThing1, prepareRenderBlock(block))
    }

    fun addNormalRender2(
        block : () -> Unit
    ) {
        normalRenderThing2 = compare(normalRenderThing2, prepareRenderBlock(block))
    }

    fun addPreRender(
        block : () -> Unit
    ) {
        preRenderThing = compare(preRenderThing, prepareRenderBlock(block))
    }

    fun addShaderRender(
        block : () -> Unit
    ) {
        shaderableThing1 = compare(shaderableThing1, prepareRenderBlock(block))
    }

    fun addShaderRender2(
        block : () -> Unit
    ) {
        shaderableThing2 = compare(shaderableThing2, prepareRenderBlock(block))
    }

    fun addPostRender(
        block : () -> Unit
    ) {
        postRenderThing1 = compare(postRenderThing1, prepareRenderBlock(block))
    }

    fun addPostRender2(
        block : () -> Unit
    ) {
        postRenderThing2 = compare(postRenderThing2, prepareRenderBlock(block))
    }

    fun addModuleComponents(
        openable : ContainableComponent,
        modules : List<Module>,
        data : Pair<Double, Int> = openable.context.offset + HEIGHT.value to openable.context.count,
        layerOffset : Int = 0,
        adder : (IComponent?) -> Unit = { },
        checker : (Module) -> Boolean = { true }
    ) = addComponents(openable, modules, data, layerOffset) { module, context ->
        if(checker(module)) {
            when(module) {
                is Hud -> HudComponent(module, context)
                else -> ModuleComponent(module, context)
            }
        } else {
            null
        }.also {
            adder(it)
        }
    }

    fun addSettingComponents(
        openable : ContainableComponent,
        settings : List<Setting<*>>,
        data : Pair<Double, Int> = openable.context.offset + HEIGHT.value to openable.context.count,
        layerOffset : Int = 0,
        adder : (IComponent?) -> Unit = { },
        checker : (Setting<*>) -> Boolean = { true }
    ) = addComponents(openable, settings, data, layerOffset) { setting, context ->
        if(checker(setting)) {
            when(setting.value) {
                is Boolean -> BooleanSettingComponent(setting as Setting<Boolean>, context)
                is Number -> NumberSettingComponent(setting as SettingNumber<*>, context)
                is Color -> ColorSettingComponent(setting as Setting<Color>, context)
                is String -> StringSettingComponent(setting as Setting<String>, context)
                is MutableList<*> -> GroupComponent(setting as SettingGroup, context)
                is Element<*> -> ComboSettingComponent(setting as Setting<Element<Any>>, context)
                is IBindable -> BindComponent(setting.value as IBindable, context.visible(setting))
                else -> null
            }.also {
                adder(it)
            }
        } else {
            null
        }
    }

    fun <T> addComponents(
        openable : ContainableComponent,
        elements : List<T>,
        data : Pair<Double, Int> = openable.context.offset + HEIGHT.value to openable.context.count,
        layerOffset : Int = 0,
        adder : (T, IComponentContext) -> IComponent?
    ) : Pair<Double, Int> {
        var offset = data.first
        var count = data.second

        for(element in elements) {
            val context = ComponentContext(openable.context.x, openable.context.y, offset, count, openable.context.layer + 1 + layerOffset)
            val component = adder(element, context)

            if(component != null) {
                openable.components.add(component)

                if(component !is FakeThing) {
                    offset += component.height.first
                    count++
                }
            }
        }

        return Pair(offset, count)
    }

    fun collectData(
        parent : IComponent
    ) : Triple<Double, Int, Int> {
        var height = parent.height.first
        var layer = parent.context.layer
        var count = parent.context.count

        if(parent is IContainable && parent.open) {
            for(component in parent.components) {
                if(component.visible()) {
                    val data = collectData(component)

                    height += data.first
                    layer = data.second
                    count = data.third
                }
            }
        }

        return Triple(height, layer, count)
    }
}