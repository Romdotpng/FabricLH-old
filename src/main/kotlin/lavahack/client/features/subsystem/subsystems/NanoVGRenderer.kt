package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.Render2DEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.compare
import lavahack.client.utils.render.screen.prepareVG
import lavahack.client.utils.render.screen.releaseVG

object NanoVGRenderer : SubSystem(
    "NanoVG Renderer"
) {
    private var task = { }
    private var empty = true

    override fun init() {
        listener<Render2DEvent.AfterScreen> {
            if(!empty) {
                prepareVG()

                task()

                releaseVG()

                task = { }
                empty = true
            }
        }
    }

    fun render(
        block : () -> Unit
    ) {
        task = compare(task, block)
        empty = false
    }
}