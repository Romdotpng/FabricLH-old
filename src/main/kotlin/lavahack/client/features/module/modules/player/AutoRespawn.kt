package lavahack.client.features.module.modules.player

import lavahack.client.event.events.ScreenEvent
import lavahack.client.features.module.Module
import lavahack.client.utils.client.interfaces.impl.listener
import net.minecraft.client.gui.screen.DeathScreen

@Module.Info(
    name = "AutoRespawn",
    description = "Instantly respawns you after death",
    category = Module.Category.PLAYER
)
class AutoRespawn : Module() {
    init {
        listener<ScreenEvent.Open> {
            if(it.screen is DeathScreen) {
                mc.player!!.requestRespawn()
                it.cancel()
            }
        }
    }
}