package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.features.subsystem.subsystems.timer
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener

@Module.Info(
    name = "Warp",
    description = "Makes you faster.",
    aliases = "TickShift",
    category = Module.Category.MOVEMENT
)
class Warp : Module() {
    init {
        val time = register(SettingNumber("Time", 10, 1..84))
        val ticks = register(SettingNumber("Ticks", 4, 1..8))
        val accelerate = register(SettingNumber("Accelerate", 0.1f, 0f..1f))
        val step = register(Setting("Step", true))

        var delay = 0
        var a = 1f

        disableCallback {
            mc.player?.stepHeight = 0.5f
            timer = 1
            delay = 0
            a = 1f

            if(step.value) {
                Step.state = false
            }
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            if(step.value) {
                Step.state = true
            }

            delay++
            a += accelerate.value

            timer = if(a < ticks.value) {
                a
            } else {
                ticks.value
            }

            if(delay >= time.value) {
                state = false
            }
        }
    }
}