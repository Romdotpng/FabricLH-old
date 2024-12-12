package lavahack.client.features.gui.searchbar

import lavahack.client.features.gui.LavaHackScreen
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.AnimatorContext
import lavahack.client.utils.client.interfaces.impl.Rect
import lavahack.client.utils.client.interfaces.impl.StateAnimator
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.math.sqrt
import lavahack.client.utils.minecraft.LavaHackSimpleTextField
import lavahack.client.utils.render.screen.*
import net.minecraft.client.gui.DrawContext
import org.joml.Vector2d
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.ranges.coerceIn

object SearchBar : LavaHackScreen(
    "Search Bar"
) {
    private val SYNC_WITH_GUI = register(Setting("Sync With Gui", false))

    private val COLORS_GROUP = register(SettingGroup("Colors").link(SYNC_WITH_GUI))
    val PRIMARY_COLOR = register(COLORS_GROUP.add(Setting("Primary Color", Colour(255, 0, 0, 255), "Primary").link(SYNC_WITH_GUI, ModuleGui.PRIMARY_COLOR)))
    val BACKGROUND_COLOR = register(COLORS_GROUP.add(Setting("Background Color", Colour(0, 0, 0, 120), "Background").link(SYNC_WITH_GUI, ModuleGui.BACKGROUND_COLOR)))
    val PRIMARY_TEXT_COLOR = register(COLORS_GROUP.add(Setting("Primary Text Color", Colour(255, 255, 255, 255), "Primary Text").link(SYNC_WITH_GUI, ModuleGui.PRIMARY_TEXT_COLOR)))
    val BACKGROUND_TEXT_COLOR = register(COLORS_GROUP.add(Setting("Background Text Color", Colour(255, 255, 255, 255), "Background Text").link(SYNC_WITH_GUI, ModuleGui.BACKGROUND_TEXT_COLOR)))

    val BACKGROUND = register(Setting("Background", true).link(SYNC_WITH_GUI, ModuleGui.BACKGROUND1))
    val SHADERED_BACKGROUND = register(Setting("Shadered Background", false).link(SYNC_WITH_GUI, ModuleGui.SHADERED_BACKGROUNDS))
    val OUTLINE = register(Setting("Outline", false).link(SYNC_WITH_GUI, ModuleGui.VERTICAL_LINES or ModuleGui.HORIZONTAL_LINES))
    val OFFSET = register(SettingNumber("Offset", 5.0, 0.0..5.0))
    val WIDTH = register(SettingNumber("Width", 150, 100..500))

    private val ANIMATIONS_GROUP = register(SettingGroup("Animations").link(SYNC_WITH_GUI))
    private val TOGGLE_ANIMATION_GROUP = register(ANIMATIONS_GROUP.add(SettingGroup("Toggle").link(SYNC_WITH_GUI)))
    private val TOGGLE_ANIMATION_STATE = register(TOGGLE_ANIMATION_GROUP.add(Setting("State", false).link(SYNC_WITH_GUI, ModuleGui.TOGGLE_ANIMATION_STATE)))
    private val TOGGLE_ANIMATION_EASING = register(TOGGLE_ANIMATION_GROUP.add(SettingEnum("Easing", Easings.Linear).link(SYNC_WITH_GUI, ModuleGui.TOGGLE_ANIMATION_EASING)))
    private val TOGGLE_ANIMATION_LENGTH = register(TOGGLE_ANIMATION_GROUP.add(SettingNumber("Length", 750L, 0L..1000L).link(SYNC_WITH_GUI, ModuleGui.TOGGLE_ANIMATION_LENGTH)))
    private val TOGGLE_ANIMATION_CIRCLE = register(TOGGLE_ANIMATION_GROUP.add(Setting("Circle", false).link(SYNC_WITH_GUI, ModuleGui.TOGGLE_ANIMATION_CIRCLE)))
    private val TOGGLE_ANIMATION_FROM_L_TO_R = register(TOGGLE_ANIMATION_GROUP.add(Setting("From Left To Right", true, "Left -> Right").link(SYNC_WITH_GUI, ModuleGui.TOGGLE_ANIMATION_FROM_L_TO_R)))
    private val TOGGLE_ANIMATION_FROM_R_TO_L = register(TOGGLE_ANIMATION_GROUP.add(Setting("From Right To Left", true, "Right -> Left").link(SYNC_WITH_GUI, ModuleGui.TOGGLE_ANIMATION_FROM_R_TO_L)))
    private val TOGGLE_ANIMATION_ALPHA = register(TOGGLE_ANIMATION_GROUP.add(Setting("Alpha", false).link(SYNC_WITH_GUI, ModuleGui.TOGGLE_ANIMATION_ALPHA)))

    private val SHADERS_GROUP = register(SettingGroup("Shaders"))
    private val SHADER = register(SHADERS_GROUP.add(SettingEnum("Shader", CoreShaders.None)))

    private val ANIMATOR_CONTEXT = AnimatorContext(TOGGLE_ANIMATION_EASING, TOGGLE_ANIMATION_LENGTH)
    private val ANIMATOR = StateAnimator(ANIMATOR_CONTEXT)

    private val TEXT_FIELD = LavaHackSimpleTextField(
        backgroundText = "Type to search a module...",
        fillCallback = { context, rect, mouseX, mouseY, selected ->
            if(BACKGROUND.value) {
                if(SHADERED_BACKGROUND.value) {
                    SHADER.beginScreen()
                }
                
                rect(
                    context,
                    rect,
                    BACKGROUND_COLOR.value
                )

                if(SHADERED_BACKGROUND.value) {
                    SHADER.endScreen()
                }
            }

            SHADER.beginScreen()

            //TODO: move it into separate method
            if(TOGGLE_ANIMATION_STATE.value) {
                val x = rect.x.toDouble()
                val y = rect.y.toDouble()
                val width = rect.w.toDouble()
                val height = rect.h.toDouble()
                val start = ANIMATOR.ENABLE_ANIMATOR.get()
                val end = ANIMATOR.ENABLE_ANIMATOR2.get()
                var color : Color = PRIMARY_COLOR.value

                if(TOGGLE_ANIMATION_ALPHA.value) {
                    color = Color(color.red, color.green, color.blue, (color.alpha * start).coerceIn(0.0..255.0).toInt())
                }

                if(TOGGLE_ANIMATION_CIRCLE.value) {
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

                    if (!selected) {
                        if (start == 1.0 && end == 1.0) {
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

                if(TOGGLE_ANIMATION_FROM_L_TO_R.value) {
                    rect(
                        context,
                        x + width * start,
                        y,
                        x + width * end,
                        y + height,
                        color
                    )
                }

                if(TOGGLE_ANIMATION_FROM_R_TO_L.value) {
                    rect(
                        context,
                        x + width * start,
                        y,
                        x + width * end,
                        y + height,
                        color
                    )
                }

                if(!TOGGLE_ANIMATION_FROM_L_TO_R.value && !TOGGLE_ANIMATION_FROM_R_TO_L.value && !TOGGLE_ANIMATION_CIRCLE.value) {
                    rect(
                        context,
                        rect,
                        color
                    )
                }

                ANIMATOR.update(selected)
            } else {
                if(selected) {
                    rect(
                        context,
                        rect,
                        PRIMARY_COLOR.value
                    )
                }
            }

            SHADER.endScreen()
        },
        outlineCallback = { context, rect ->
            if(OUTLINE.value) {
                SHADER.drawScreen {
                    outlineRect(
                        context,
                        rect,
                        PRIMARY_COLOR.value.clone().alpha(255),
                        1
                    )
                }
            }
        },
        textCallback = { context, rect, text, backgroundText, selected, background ->
            val color = if(selected) {
                PRIMARY_TEXT_COLOR
            } else {
                BACKGROUND_TEXT_COLOR
            }.value

            val fixedText = if(selected) "${text}_" else backgroundText
            val x = rect.x + OFFSET.value
            val y = rect.y + OFFSET.value

            drawString(
                context,
                fixedText,
                x,
                y,
                color,
                shadow = true
            )
        }
    )

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) {
        val rating = ModuleRating()
        for (category in Module.Category.values()) {
            rating.modules.addAll(category.modules)
        }

        val visibleModules = rating.modulesVisibility(TEXT_FIELD.text, precision = true, aliases = true)

        for (category in Module.Category.values()) {
            for (module in category.modules) {
                if (TEXT_FIELD.text.trim().isEmpty()) {
                    module.guiVisible = true

                    continue
                }

                module.guiVisible = visibleModules.contains(module)
            }
        }

        val height = OFFSET.value * 2 + fontHeight()
        val x = mc.window.scaledWidth / 2.0 - WIDTH.value / 2.0
        val y = mc.window.scaledHeight - height - 30
        val rect = Rect(x, y, WIDTH.value, height)

        TEXT_FIELD.render(context, mouseX, mouseY, rect)
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) = TEXT_FIELD.mouseClicked(mouseX, mouseY, button)

    override fun keyPressed(
        keyCode : Int,
        scancode : Int,
        modifiers : Int
    ) = true.also {
        TEXT_FIELD.keyPressed(keyCode)
    }

    override fun charTyped(
        char : Char,
        modifiers : Int
    ) = true.also {
        TEXT_FIELD.charTyped(char)
    }
}