package lavahack.client.event.events

import lavahack.client.event.bus.Event
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

open class ScreenEvent : Event() {
    class Open(
        val screen : Screen?
    ) : ScreenEvent() {
        companion object {
            var STATE = true
        }
    }

    class Render {
        class Pre(val screen : Screen, val context : DrawContext) : ScreenEvent()
        class Post(val screen : Screen, val context : DrawContext) : ScreenEvent()

        companion object {
            var RENDERING = false
        }
    }
}