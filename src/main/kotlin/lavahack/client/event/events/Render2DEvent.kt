package lavahack.client.event.events

import lavahack.client.event.bus.Event
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 19:30 of 08.05.2023
 */
open class Render2DEvent : Event() {
    class Pre(val context : DrawContext, val delta : Float) : Render2DEvent()
    class Post(val context : DrawContext, val delta : Float) : Render2DEvent()

    class AfterScreen(val context : DrawContext) : Render2DEvent()

    class Overlay {
        class Portal : Render2DEvent()
        class PowderSnow : Render2DEvent()
        class Pumpkin : Render2DEvent()
        class Spyglass : Render2DEvent()
        class Crosshair : Render2DEvent()
    }
}