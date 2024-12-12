package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.ModuleGui.animatedRect
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.ToggleableComponent
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.DUMMY_RECT
import lavahack.client.utils.compare
import lavahack.client.utils.minecraft.LavaHackSimpleTextField
import net.minecraft.client.gui.DrawContext

@Suppress("PrivatePropertyName")
class StringSettingComponent(
    setting : Setting<String>,
    context : IComponentContext
) : ToggleableComponent(
    context.visible(setting),
    null,
    { }
) {
    private val TEXT_FIELD = LavaHackSimpleTextField(
        backgroundText = setting.title,
        fillCallback = { context, _, mouseX, mouseY, _ ->
            ModuleGui.backgrounds(context, this, mouseX, mouseY)
            animatedRect(context, offsets = true)
        },
        outlineCallback = { context, _ ->
            ModuleGui.lines(context, this)
            ModuleGui.outline(context, this)
        },
        textCallback = { context, _, text, backgroundText, selected, _ ->
            val currentText = if(selected) "${text}_" else "${backgroundText}: $text"

            ModuleGui.drawStringWithShadow(
                context,
                currentText,
                this,
                primary = selected
            )
        },
        textSubmitter = { text ->
            setting.value = text
        }
    )

    init {
        TEXT_FIELD.text = setting.value
        setting.onChange = compare(setting.onChange) { TEXT_FIELD.text = it.value }
        toggleCallback = {
            TEXT_FIELD.focused = it
        }
    }

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        //TODO: remove DUMMY_RECT usage
        TEXT_FIELD.render(context, mouseX, mouseY, DUMMY_RECT)
    }

    override fun keyPressed(
        code : Int,
        scan : Int,
        modifiers : Int
    ) {
        TEXT_FIELD.keyPressed(code)
    }

    override fun charTyped(
        char : Char,
        modifiers : Int
    ) {
        TEXT_FIELD.charTyped(char)
    }
}