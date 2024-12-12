package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.Component
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.ContainableComponent
import lavahack.client.settings.Setting
import lavahack.client.settings.types.combo.Element
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 9:32 of 19.05.2023
 */
@Suppress("UNUSED_PARAMETER")
class ComboSettingComponent(
    private val combo : Setting<Element<Any>>,
    context : IComponentContext
) : ContainableComponent(
    context.visible(combo)
) {
    /**
     * 0 - closed
     *
     * 1 - opened element selector
     *
     * 2 - opened element binder
     */
    private var state = 0

    override var realOpen : Boolean
        get() = state != 0
        set(value) {}

    init {
        super.onClick = {
            state = if(state == 0) {
                it + 1
            } else {
                0
            }
        }

        ModuleGui.addComponents(this, combo.value.list) { element, context ->
            Option(
                this,
                combo,
                element,
                context
            )
        }
    }

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        ModuleGui.fill(context, this, true)
        ModuleGui.drawStringWithShadow(context, "${combo.title}: ${combo.value.current}", this)
    }

    class Option<T>(
        private val parent : ComboSettingComponent,
        private val combo : Setting<Element<T>>,
        private val element : T,
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

            ModuleGui.drawStringWithShadow(
                context,
                element.toString(),
                this
            )
        }

        override fun mouseClicked(
            mouseX : Double,
            mouseY : Double,
            button : Int
        ) {
            super.mouseClicked(mouseX, mouseY, button)

            if(hovering(mouseX, mouseY) && button == 0) {
                combo.value.current = element
                parent.state = 0
            }
        }

        override fun visible() = parent.state == 1 && combo.value.current != element
    }
}