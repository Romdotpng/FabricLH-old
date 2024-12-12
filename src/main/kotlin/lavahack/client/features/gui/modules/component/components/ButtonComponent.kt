package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.Component
import lavahack.client.features.gui.modules.component.IComponentContext
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 7:48 of 31.07.2023
 */
class ButtonComponent(
    private val text : String,
    private val action : () -> Unit,
    context : IComponentContext
) : Component(
    context
) {
    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        ModuleGui.drawStringWithShadow(context, text, this)
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        if(hovering(mouseX, mouseY)) {
            action()
        }
    }
}