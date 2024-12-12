package lavahack.client.features.gui.modules.component.components

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.DescriptionContext
import lavahack.client.features.gui.modules.component.IComponent
import lavahack.client.utils.render.screen.stringWidth
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 15:10 of 17.07.2023
 */
@Suppress("UNUSED_PARAMETER")
class DescriptionComponent(
    private val parent : IComponent,
    private val description : String
) : IComponent {
    override val context = DescriptionContext(parent.context)

    override var width
        get() = ModuleGui.TEXT_OFFSET_X.value * 2.0 + stringWidth(description)
        set(value) {}

    override var height
        get() = parent.height
        set(value) {}

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        if(ModuleGui.DESCRIPTION.value && hovering(mouseX.toDouble(), mouseY.toDouble()) && description.isNotEmpty()) {
            ModuleGui.rect(
                context,
                this,
                ModuleGui.DESCRIPTION_COLOR.value,
                false
            )
        }
    }

    override fun hovering(
        mouseX : Double,
        mouseY : Double
    ) = mouseX >= context.x && mouseX <= context.x + width && mouseY >= context.y + context.offset && mouseY <= context.y + context.offset + height.first
}