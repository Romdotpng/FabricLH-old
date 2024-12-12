package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.ModuleGui.animatedRect
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.ToggleableComponent
import lavahack.client.settings.Setting
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 14:59 of 10.05.2023
 */
class BooleanSettingComponent(
    private val setting : Setting<Boolean>,
    context : IComponentContext
) : ToggleableComponent(
    context.visible(setting),
    { setting.value },
    { setting.value = it },
) {
    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        animatedRect(context, offsets = true)

        ModuleGui.drawStringWithShadow(context, setting.title, this)
    }
}