@file:Suppress("UNUSED_PARAMETER")

package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.Component
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.ContainableComponent
import lavahack.client.features.subsystem.subsystems.ColorManager
import lavahack.client.settings.Setting
import lavahack.client.utils.*
import lavahack.client.utils.render.screen.rectWH
import net.minecraft.client.gui.DrawContext
import java.awt.Color

/**
 * @author _kisman_
 * @since 6:33 of 16.05.2023
 */
class ColorSettingComponent(
    val color : Setting<Color>,
    context : IComponentContext
) : ContainableComponent(
    context.visible(color),
    btn = 0
) {
    init {
        val base = Base(this)
        val hue = Hue(this)
        val alpha = Alpha(this)
        val copy = ButtonComponent("Copy", { ColorManager.COPIED_COLOR = color.value }, context.clone().layer(context.layer + 1).visible(ColorManager.COPY_PASTE_VISIBILITY))
        val paste = ButtonComponent("Paste", { if(color.value is Colour) { (color.value as Colour).copy(ColorManager.COPIED_COLOR ?: color.value) ; color.onChange(color) } else color.value = ColorManager.COPIED_COLOR ?: color.value }, context.clone().layer(context.layer + 1).visible(ColorManager.COPY_PASTE_VISIBILITY))
//        val sync = ButtonComponent("Sync", { color.value = ColorManager.SYNC_COLOR.value }, x, y, offset, count, layer + 1, ColorManager.SYNC_COLOR_VISIBILITY)

        components.add(base)
        components.add(hue)
        components.add(alpha)
        components.add(copy)
        components.add(paste)
//        components.add(sync)

        base.context.offset = this.context.offset + height.first
        hue.context.offset = base.context.offset + base.height.first
        alpha.context.offset = hue.context.offset + hue.height.first
        copy.context.offset = alpha.context.offset + alpha.height.first
        paste.context.offset = copy.context.offset + copy.height.first
//        sync.offset = paste.offset + paste.height.first

        if(color.value is Colour) {
            ModuleGui.addSettingComponents(this, (color.value as Colour).registry.settings) { it.bound == color.value }
        }
    }

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        ModuleGui.drawStringWithShadow(context, color.title, this)
        ModuleGui.rect(context, this, color.value, true)
    }

    class Base(
        private val parent : ColorSettingComponent
    ) : Component(
        parent.context.clone().layer(parent.context.layer + 1)
    ) {
        override var height
            get() = width.toDouble() to width.toDouble()
            set(value) {}

        private var dragging = false

        override fun render(
            context : DrawContext,
            mouseX : Int,
            mouseY : Int
        ) {
            super.render(context, mouseX, mouseY)

            val hsb = parent.color.value.toHSB()

            ModuleGui.gradientRect(
                context,
                this,
                Color.WHITE,
                parent.color.value.saturation(1).brightness(1),
                Color.BLACK,
                Color.BLACK,
                true
            )

            ModuleGui.addPostRender2 {
                rectWH(
                    context,
                    this.context.x + ModuleGui.layerOffset(this) + ModuleGui.OFFSETS_X.value + (width - ModuleGui.OFFSETS_X.value * 2) * hsb[1] - 1,
                    this.context.y + this.context.offset + ModuleGui.OFFSETS_Y.value + height.first * (1f - hsb[2]) - 1,
                    2,
                    2,
                    Color.WHITE
                )
            }
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
                val saturation = percent(mouseX, context.x + ModuleGui.layerOffset(this) + ModuleGui.OFFSETS_X.value, width).coerceIn(0.0..1.0)
                val brightness = 1.0 - percent(mouseY, context.y + context.offset, height.first).coerceIn(0.0..1.0)

                if(parent.color.value is Colour) {
                    (parent.color.value as Colour).hsb(_saturation = saturation, _brightness = brightness)
                    parent.color.onChange(parent.color)
                } else {
                    parent.color.value = parent.color.value.saturation(saturation).brightness(brightness)
                }
            }
        }

        private fun percent(
            mouseA : Number,
            a : Number,
            lengthA : Number
        ) = (mouseA.toDouble() - a.toDouble()) / lengthA.toDouble()
    }

    class Hue(
        private val parent : ColorSettingComponent
    ) : Component(
        parent.context.clone().layer(parent.context.layer + 1)
    ) {
        private var dragging = false

        override fun render(
            context : DrawContext,
            mouseX : Int,
            mouseY : Int
        ) {
            super.render(context, mouseX, mouseY)

            ModuleGui.rect(context, this, Color.RED, true)

            context.matrices.push()
            context.matrices.translate(this.context.x + ModuleGui.layerOffset(this) + ModuleGui.OFFSETS_X.value, this.context.y + this.context.offset + ModuleGui.OFFSETS_Y.value, 0.0)

            for(pixel in ModuleGui.OFFSETS_X.value.toInt() until (width - ModuleGui.OFFSETS_X.value * 2).toInt()) {
                val hue = pixel / (width - ModuleGui.OFFSETS_X.value * 2 - 1).toFloat()
                val color = Color.getHSBColor(hue, 1f, 1f)

                rectWH(
                    context,
                    pixel,
                    0,
                    1,
                    height.first - ModuleGui.OFFSETS_Y.value * 2,
                    color
                )
            }

            context.matrices.pop()

            val hue = parent.color.value.toHSB()[0]

            ModuleGui.addPostRender2 {
                rectWH(
                    context,
                    this.context.x + ModuleGui.layerOffset(this) + ModuleGui.OFFSETS_X.value + (width - ModuleGui.OFFSETS_X.value * 2) * hue - 1,
                    this.context.y + this.context.offset + ModuleGui.OFFSETS_Y.value,
                    2,
                    height.first - ModuleGui.OFFSETS_Y.value * 2,
                    Color.WHITE
                )
            }
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
                val hue = percent(mouseX, context.x + ModuleGui.layerOffset(this) + ModuleGui.OFFSETS_X.value, width - ModuleGui.OFFSETS_X.value * 2).coerceIn(0.0..1.0)

                if(parent.color.value is Colour) {
                    (parent.color.value as Colour).hsb(_hue = hue)
                    parent.color.onChange(parent.color)
                } else {
                    parent.color.value = parent.color.value.hue(hue)
                }
            }
        }

        private fun percent(
            mouseA : Number,
            a : Number,
            lengthA : Number
        ) = (mouseA.toDouble() - a.toDouble()) / lengthA.toDouble()
    }

    class Alpha(
        private val parent : ColorSettingComponent
    ) : Component(
        parent.context.clone().layer(parent.context.layer + 1)
    ) {
        private var dragging = false

        override fun render(
            context : DrawContext,
            mouseX : Int,
            mouseY : Int
        ) {
            super.render(context, mouseX, mouseY)

            val color1 = Color(parent.color.value.red, parent.color.value.green, parent.color.value.blue, 0)
            val color2 = Color(parent.color.value.red, parent.color.value.green, parent.color.value.blue, 255)
            val alpha = (parent.color.value.alpha / 255.0).coerceIn(0.0..1.0)

            ModuleGui.gradientRect(
                context,
                this,
                color1,
                color2,
                color1,
                color2,
                true
            )

            ModuleGui.addPostRender2 {
                rectWH(
                    context,
                    this.context. x + ModuleGui.layerOffset(this) + ModuleGui.OFFSETS_X.value + (width - ModuleGui.OFFSETS_X.value * 2) * alpha - 1,
                    this.context.y + this.context.offset + ModuleGui.OFFSETS_Y.value,
                    2,
                    height.first - ModuleGui.OFFSETS_Y.value * 2,
                    Color.WHITE
                )
            }
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
                val alpha = (percent(mouseX, context.x + ModuleGui.layerOffset(this) + ModuleGui.OFFSETS_X.value, width - ModuleGui.OFFSETS_X.value * 2) * 255.0).toInt().coerceIn(0..255)

                if(parent.color.value is Colour) {
                    (parent.color.value as Colour).alpha(alpha)
                    parent.color.onChange(parent.color)
                } else {
                    parent.color.value = Color(parent.color.value.red, parent.color.value.green, parent.color.value.blue, alpha)
                }
            }
        }

        private fun percent(
            mouseA : Number,
            a : Number,
            lengthA : Number
        ) = (mouseA.toDouble() - a.toDouble()) / lengthA.toDouble()
    }
}