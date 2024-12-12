package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.Component
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.animate
import lavahack.client.utils.client.ranges.WrappedClosedRange
import lavahack.client.utils.math.roundToPlace
import lavahack.client.utils.step
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 12:45 of 12.05.2023
 */
class NumberSettingComponent(
    private val setting : SettingNumber<*>,
    context : IComponentContext
) : Component(
    context.visible(setting)
) {
    private var dragging = false

    private var animatedWidth = rawWidth()

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        ModuleGui.fill(context, this, true)
        ModuleGui.drawStringWithShadow(context, "${setting.title}: ${roundToPlace(setting.value as Number, 2)}", this)
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        dragging = hovering(mouseX, mouseY) && button == 0

        mouseDragged(mouseX, mouseY, button, 0.0, 0.0)
    }

    override fun mouseReleased(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseReleased(mouseX, mouseY, button)

        dragging = false
    }

    override fun mouseDragged(
        mouseX : Double,
        mouseY : Double,
        button : Int,
        deltaX : Double,
        deltaY : Double
    ) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)

        if(dragging) {
            val min = (setting.range.start as Number).toDouble()
            val max = (setting.range.endInclusive as Number).toDouble()

            val percent = percent(mouseX)
            var value = (min + (max - min) * percent).coerceIn(min..max)

            run {
                if (setting.range is WrappedClosedRange) {
                    for (i in min..max step setting.range.step) {
                        if (i > value && i < value + setting.range.step) {
                            value = i

                            return@run
                        }
                    }
                }
            }

            setting.set(value)
        }
    }

    override fun width() = animatedWidth

    override fun preRenderTick() {
        super.preRenderTick()

        val currentWidth = rawWidth()

        if(currentWidth != animatedWidth) {
            animatedWidth = if(ModuleGui.SLIDER_ANIMATION_STATE.value) {
                animate(animatedWidth, currentWidth, ModuleGui.SLIDER_ANIMATION_SPEED.value)
            } else {
                currentWidth
            }
        }
    }

    private fun rawWidth() : Double {
        val min = (setting.range.start as Number).toDouble()
        val max = (setting.range.endInclusive as Number).toDouble()
        val current = (setting.value as Number).toDouble()

        return ((current - min) / (max - min)) * (width - ModuleGui.OFFSETS_X.value * 2)
    }

    private fun percent(
        mouseX : Number
    ) = (mouseX.toDouble() - context.x - ModuleGui.layerOffset(this) - ModuleGui.OFFSETS_X.value) / width
}