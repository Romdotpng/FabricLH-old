package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.ModuleGui.animatedRect
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.ToggleableComponent
import lavahack.client.utils.client.enums.BindTypes
import lavahack.client.utils.client.interfaces.IBindable
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 7:42 of 20.05.2023
 */
class BindComponent(
    private val bindable : IBindable,
    context : IComponentContext
) : ToggleableComponent(
    context,
    null,
    { }
) {
    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        animatedRect(context, offsets = true)

        ModuleGui.drawStringWithShadow(
            context,
            if(state) "Press a key"
            else "${bindable.buttonName}: ${IBindable.getName(bindable)}",
            this
        )
    }

    override fun keyPressed(
        code : Int,
        scan : Int,
        modifiers : Int
    ) {
        super.keyPressed(code, scan, modifiers)

        if(state) {
            state = false
            bindable.type = BindTypes.Keyboard
            bindable.keyboardKey = code
        }
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        if(state && button > 1) {
            state = false
            bindable.type = BindTypes.Mouse
            bindable.mouseButton = button
        }

        if(hovering(mouseX, mouseY) && button == 1) {
            state = false
            bindable.type = BindTypes.Keyboard
            bindable.keyboardKey = -1
            bindable.mouseButton = -1
        }
    }
}