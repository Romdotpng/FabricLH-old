package lavahack.client.features.gui.huds.components

import lavahack.client.features.gui.modules.component.IComponentContext
import lavahack.client.features.gui.modules.component.components.ModuleComponent
import lavahack.client.features.hud.Hud
import net.minecraft.client.gui.DrawContext

class HudComponent(
    private val hud : Hud,
    context : IComponentContext
) : ModuleComponent(
    hud,
    context
) {
    private val draggable = DraggableComponent(hud.HITBOX)

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        if(hud.state) {
            draggable.render(context, mouseX, mouseY)
        }
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        if(hud.state) {
            draggable.mouseClicked(mouseX, mouseY, button)
        }
    }

    override fun mouseReleased(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseReleased(mouseX, mouseY, button)

        if(hud.state) {
            draggable.mouseReleased(mouseX, mouseY, button)
        }
    }


    override fun mouseDragged(
        mouseX : Double,
        mouseY : Double,
        button : Int,
        deltaX : Double,
        deltaY : Double
    ) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)

        if(hud.state) {
            draggable.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }
    }
}