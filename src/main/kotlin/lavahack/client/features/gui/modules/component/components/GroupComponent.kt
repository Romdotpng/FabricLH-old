package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.ContainableComponent
import lavahack.client.settings.types.SettingGroup
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 9:42 of 15.05.2023
 */
class GroupComponent(
    private val group : SettingGroup,
    context : IComponentContext
) : ContainableComponent(
    context.visible(group),
    btn = 0
) {
    init {
        ModuleGui.addSettingComponents(this, group.registry.settings) { it.bound == group }
    }

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        ModuleGui.fill(context, this, true)
        ModuleGui.drawStringWithShadow(context, "${group.title}...", this)
    }
}