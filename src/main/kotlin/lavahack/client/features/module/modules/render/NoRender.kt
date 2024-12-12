package lavahack.client.features.module.modules.render

import lavahack.client.event.bus.Event
import lavahack.client.event.bus.Listener
import lavahack.client.event.events.Render2DEvent
import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.listeners
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 12:00 of 26.05.2023
 */
@Module.Info(
    name = "NoRender",
    category = Module.Category.RENDER
)
class NoRender : Module() {
    init {
        fun createPair(
            name : String,
            type : Class<out Event>
        ) {
            val state = register(Setting(name, false))
            val listener = Listener(type) { if(state.value) it.cancel() }

            listeners(listener)
        }

        createPair("Default Highlight", Render3DEvent.DefaultBlockOutline::class.java)
        createPair("Crosshair", Render2DEvent.Overlay.Crosshair::class.java)
        createPair("Pumpkin", Render2DEvent.Overlay.Pumpkin::class.java)
        createPair("Portal", Render2DEvent.Overlay.Portal::class.java)
        createPair("Powder Snow", Render2DEvent.Overlay.PowderSnow::class.java)
        createPair("Spyglass", Render2DEvent.Overlay.Spyglass::class.java)
        createPair("World Border", Render3DEvent.WorldBorder::class.java)
        createPair("Weather", Render3DEvent.Weather::class.java)
        createPair("Entity Fire", Render3DEvent.EntityFire::class.java)
        createPair("Entity Nametag", Render3DEvent.EntityNametag::class.java)
        createPair("Hurt Camera Effect", Render3DEvent.TiltView::class.java)
        createPair("Underwater", Render3DEvent.Overlay.Underwater::class.java)
        createPair("Fire", Render3DEvent.Overlay.Fire::class.java)
    }
}