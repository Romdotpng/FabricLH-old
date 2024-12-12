package lavahack.client.features.gui.modules

import lavahack.client.features.gui.modules.component.XYContext
import lavahack.client.features.gui.modules.component.IContainable
import lavahack.client.features.gui.modules.component.ContainableComponent
import lavahack.client.features.module.Module
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.FakeThing
import lavahack.client.utils.render.screen.rectWH
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 10:43 of 10.05.2023
 */
@Suppress("LeakingThis")
open class Frame(
    val modules : List<Module>,
    val name : String,
    x : Double,
    y : Double
) : ContainableComponent(
    XYContext(x, y)
) {
    private var dragging = false
    private var dragX = 0.0
    private var dragY = 0.0

    override var height : Pair<Double, Double>
        get() = (ModuleGui.HEIGHT.value + if(open) ModuleGui.OFFSETS_Y.value else 0.0) to ModuleGui.HEIGHT.value.toDouble()
        set(value) { }

    init {
        ModuleGui.addModuleComponents(this, modules, layerOffset = -1)

        open = true
    }

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        ModuleGui.fill(context, this, false, defaultOffsetX = if(ModuleGui.VERTICAL_LINES.value) 1.0 else 0.0, defaultTopOffsetY = if(ModuleGui.HORIZONTAL_LINES.value) 1.0 else 0.0)
        ModuleGui.lines(context, this)
        ModuleGui.drawStringWithShadow(context, name, this)

        //TODO: refactor that
        if(ModuleGui.OFFSETS_Y.value != 0.0) {
            val background = {
                rectWH(
                    context,
                    this.context.x + if(ModuleGui.VERTICAL_LINES.value) 1.0 else 0.0,
                    this.context.y + height.first,
                    width - if(ModuleGui.VERTICAL_LINES.value) 2.0 else 0.0,
                    -ModuleGui.OFFSETS_Y.value,
                    ModuleGui.BACKGROUND_COLOR.value
                )
            }

            val lines = {
                val color = if(ModuleGui.OVERRIDE_LINES_ALPHA.value) Colour(ModuleGui.PRIMARY_COLOR.value.rgb).alpha(255) else ModuleGui.PRIMARY_COLOR.value

                rectWH(
                    context,
                    this.context.x + if(ModuleGui.VERTICAL_LINES.value) 1.0 else 0.0,
                    this.context.y,
                    width - if(ModuleGui.VERTICAL_LINES.value) 2.0 else 0.0,
                    1,
                    color
                )
            }

            if(open && ModuleGui.BACKGROUND1.value) {
                if(ModuleGui.SHADERED_BACKGROUNDS.value) {
                    ModuleGui.addPreRender(background)
                } else {
                    background()
                }
            }

            if(ModuleGui.HORIZONTAL_LINES.value) {
                ModuleGui.addShaderRender(lines)
            }
        }

        if(open) {
            for(component in components) {
                if(component.visible()) {
                    component.handleRender(context, mouseX, mouseY)
                }
            }
        }
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        if(button == 0 && hovering(mouseX, mouseY)) {
            dragging = true
            dragX = mouseX - context.x
            dragY = mouseY - context.y
        }
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
            update(mouseX.toInt() - dragX, mouseY.toInt() - dragY)
        }
    }

    fun refresh() {
        fun iteration(
            containable : IContainable,
            current : Pair<Double, Int>
        ) : Pair<Double, Int> = if(containable.open) {
            var offsetY = current.first
            var index = current.second

            for(component in containable.components) {
                if(component.visible() && component !is FakeThing) {
                    component.context.offset = offsetY
                    component.context.count = index

                    offsetY += component.height.first

                    if(component is IContainable) {
                        val data = iteration(component, offsetY to index)

                        offsetY += component.fullHeight
                        index = data.second
                    } else {
                        index++
                    }
                }
            }

            offsetY to index
        } else {
            current
        }

        iteration(this, height.first to 1)
    }
}