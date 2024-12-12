package lavahack.client.features.hud

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.DefaultAnimatorController
import lavahack.client.features.subsystem.subsystems.GRAY
import lavahack.client.features.subsystem.subsystems.colored
import lavahack.client.features.subsystem.subsystems.text
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.HudBackgroundPattern
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Colour
import lavahack.client.utils.client.collections.Sorter
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.Orientations
import lavahack.client.utils.client.interfaces.IAnimatorContext
import lavahack.client.utils.client.interfaces.IStateAnimator
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.render.screen.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.awt.Color
import kotlin.math.max

/**
 * @author _kisman_
 * @since 19:18 of 08.05.2023
 */
@Suppress("PropertyName")
open class Hud : Module() {
    val HITBOX = Hitbox { state }

    init {
        register(Setting("Hitbox", HITBOX))
    }

    override fun prefix() = "hud.${info.name}"

    override fun save() = super.save().also {
        it.prefix = "hud.${info.name}"
    }

    open class Single(
        private val key : () -> MutableText,
        private val value : () -> MutableText,
        private val delimiter : String = ""
    ) : Hud() {
        init {
            val color = register(Setting("Color", Colour(255, 0, 0, 255)))

            screenListener {
                val text = Text.literal("").append(colored(key(), color.value.text()))

                if(value().string.isNotEmpty()) {
                    text
                        .append(colored(delimiter, GRAY))
                        .append(colored(value(), GRAY))
                }

                drawString(it.context, text, HITBOX.x, HITBOX.y, Color.WHITE, true)
                HITBOX.w = stringWidth(text.string).toFloat()
                HITBOX.h = fontHeight().toFloat()
            }
        }
    }

    abstract class Multi(
        default : Boolean = false
    ) : Hud() {
        val ANIMATOR : IAnimatorContext

        init {
            val sorter = Sorter<Entry> { it.text.string }

            val color = register(Setting("Color", Colour(255, 0, 0, 255)))
            val sort = register(Setting("Sort", sorter.asElement()))
            val reverse = register(Setting("Reverse", false))
            val orientation = register(SettingEnum("Orientation", Orientations.Left))
            val offsetX = register(SettingNumber("Offset X", 2, 0..5))
            val offsetY = register(SettingNumber("Offset Y", 2, 0..5))

            val animationGroup = register(SettingGroup("Animations"))
            val animationState = register(animationGroup.add(Setting("State", false)))

            val animationEasing = register(animationGroup.add(if(default) DefaultAnimatorController.DEFAULT_EASING else SettingEnum("Easing", Easings.Linear)))
            val animationLength = register(animationGroup.add(if(default) DefaultAnimatorController.DEFAULT_LENGTH else SettingNumber("Length", 750L, 100L..1000L)))
            val animationNonstop = register(animationGroup.add(Setting("Nonstop", false)))

            val backgroundGroup = register(SettingGroup("Background"))
            val backgroundState = register(backgroundGroup.add(Setting("State", false)))
            val backgroundColor = register(backgroundGroup.add(Setting("Color", Colour(0, 0, 0, 120))))

            animationGroup.prefix("Animation")
            backgroundGroup.prefix("Background")

            ANIMATOR = if(default) {
                DefaultAnimatorController.DEFAULT_ANIMATOR_CONTEXT
            } else {
                AnimatorContext(animationEasing, animationLength)
            }

            screenListener {
                val elements = mutableListOf<Entry>()
                val height = fontHeight() + offsetY.value * 2.0

                var maxLength = -1.0
                var count0 = 0

                elements(elements)
                elements.sortWith(sort.value.current.comparator)

                if(reverse.value) {
                    elements.reverse()
                }

                for(element in elements) {
                    fun updateEnableAnimation() : Double {
                        element.animator.animator.ENABLE_ANIMATOR.update()
                        element.animator.animator.DISABLE_ANIMATOR.reset()

                        return element.animator.animator.ENABLE_ANIMATOR.get()
                    }

                    fun updateDisableAnimation() : Double {
                        element.animator.animator.ENABLE_ANIMATOR.reset()
                        element.animator.animator.DISABLE_ANIMATOR.update()

                        return element.animator.animator.DISABLE_ANIMATOR.get()
                    }

                    val coeff = if(element.active()) {
                        if(animationState.value) {
                            if(animationNonstop.value && element.animator.animator.DISABLE_ANIMATOR.animating()) {
                                updateDisableAnimation()
                            } else {
                                updateEnableAnimation()
                            }
                        } else {
                            1.0
                        }
                    } else {
                        if(animationState.value) {
                            if(animationNonstop.value && element.animator.animator.ENABLE_ANIMATOR.animating()) {
                                updateEnableAnimation()
                            } else {
                                updateDisableAnimation()
                            }
                        } else {
                            0.0
                        }
                    }

                    if(coeff != 0.0) {
                        fun x(
                            string : Boolean = false
                        ) = HITBOX.x + when(orientation.valEnum) {
                            Orientations.Left -> if(string) {
                                -(stringWidth(element.text.string).toDouble() * (1.0 - coeff))
                            } else {
                                0.0
                            }

                            Orientations.Right -> {
                                HITBOX.w - stringWidth(element.text.string).toDouble() * if(string) {
                                    coeff
                                } else {
                                    1.0
                                } - offsetX.value * 2.0
                            }
                        } + if(string) {
                            offsetX.value
                        } else {
                            0
                    }

                    val count1 = count0

                    fun y(
                        string : Boolean = false
                    ) = HITBOX.y + height * count1 + if(string) {
                        offsetY.value
                    } else {
                        0
                    }

                    if(backgroundState.value) {
                            val runnable = {
                                rectWH(
                                    it.context,
                                    x(),
                                    y(),
                                    stringWidth(element.text.string) + offsetX.value * 2,
                                    fontHeight() + offsetY.value * 2,
                                    backgroundColor.value
                                )
                            }

                            runnable()
                        }

                        val runnable = {
                            drawString(
                                it.context,
                                element.text,
                                x(true),
                                y(true),
                                color.value,
                                true
                            )
                        }

                        runnable()

                        val length = stringWidth(element.text.string).toDouble()

                        maxLength = max(maxLength, length)
                        count0++
                    }
                }

                HITBOX.w = maxLength.toFloat() + offsetX.value * 2f
                HITBOX.h = height.toFloat() * count0
            }
        }

        abstract fun elements(
            list : MutableList<Entry>
        )

        class Entry(
            val animator : IStateAnimator,
            val text : MutableText,
            val active : () -> Boolean
        )
    }

    open class ItemList(
        validator : (Int) -> Boolean,
        itemGetter : (Int) -> Pair<Item, String>,
        textColorer : (Int) -> Color = { Color.WHITE },
        defaultStyle : Styles = Styles.Quad
    ) : Hud() {
        init {
            val style = register(SettingEnum("Style", defaultStyle))
            val offset = register(SettingNumber("Offset", 0, 0..10))
            val textScale = register(SettingNumber("Text Scale", 0.5, 0.5..1.0))

            val background = register(HudBackgroundPattern())

            screenListener {
                val centerX = HITBOX.x
                val centerY = HITBOX.y

                background.render(
                    it.context,
                    centerX,
                    centerY,
                    HITBOX.w,
                    HITBOX.h
                )

                for(i in 0..3) {
                    if(validator(i)) {
                        val pair = itemGetter(i)
                        val item = pair.first
                        val stack = ItemStack(item)
                        val text = pair.second
                        val color = textColorer(i)

                        val offsets = style.valEnum.offsets[i]
                        val offsetX = offsets.first + offset.value * (offsets.first / 16 + 1f)
                        val offsetY = offsets.second + offset.value * (offsets.second / 16 + 1f)

                        val x = centerX + offsetX
                        val y = centerY + offsetY

                        item(
                            it.context,
                            stack,
                            x,
                            y,
                            text,
                            textColor = color,
                            textScale = textScale.value
                        )
                    }
                }

                HITBOX.w = style.valEnum.width.toFloat()
                HITBOX.h = style.valEnum.height.toFloat()

                HITBOX.w += (HITBOX.w / 16f + 1f) * offset.value.toFloat()
                HITBOX.h += (HITBOX.h / 16f + 1f) * offset.value.toFloat()
            }
        }

        enum class Styles(
            val width : Int,
            val height : Int,
            vararg val offsets : Pair<Int, Int>
        ) {
            Vertical(
                16,
                64,
                0 to 0,
                0 to 16,
                0 to 32,
                0 to 48
            ),

            Horizontal(
                64,
                16,
                0 to 0,
                16 to 0,
                32 to 0,
                48 to 0
            ),

            Quad(
                32,
                32,
                0 to 0,
                16 to 0,
                0 to 16,
                16 to 16
            )
        }
    }
}