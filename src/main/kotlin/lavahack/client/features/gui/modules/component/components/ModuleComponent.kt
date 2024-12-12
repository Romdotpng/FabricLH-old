package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.ModuleGui.animatedRect
import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.ToggleableComponent
import lavahack.client.features.module.Module
import lavahack.client.utils.client.interfaces.IBindable
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 14:57 of 10.05.2023
 */
open class ModuleComponent(
    private val module : Module,
    context : IComponentContext
) : ToggleableComponent(
    context.visible(module),
    { module.state },
    { module.toggle(it) },
    module.info.toggleable
) {
    private val description = DescriptionComponent(this, module.info.description)

    init {
        val data = ModuleGui.addSettingComponents(this, module.registry.settings) { it.bound == module }

        ModuleGui.addModuleComponents(this, module.submodules, Pair(data.first + ModuleGui.HEIGHT.value, data.second + 1))
    }

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        animatedRect(context, offsets = true)

        ModuleGui.drawStringWithShadow(context, module.name, this, primary = stateSupplier())
        ModuleGui.drawStringWithShadow(context, if(open) "-" else "+", this, false)

        ModuleGui.addPostRender {
            if(module.info.beta) {
                ModuleGui.drawSuffix(
                    context,
                    module.name,
                    "beta",
                    this,
                    1
                )
            }

            if(IBindable.valid(module)) {
                ModuleGui.drawSuffix(
                    context,
                    module.name,
                    IBindable.getName(module),
                    this,
                    3
                )
            }
        }

        ModuleGui.addPostRender2 {
            description.render(context, mouseX, mouseY)
        }
    }

    override fun visible() = super.visible() && module.guiVisible
}