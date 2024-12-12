package lavahack.client.features.module.modules.misc

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.impl.register
import net.minecraft.client.option.Perspective

@Module.Info(
    name = "FreeLook",
    description = "360 view around your player",
    category = Module.Category.MISC
)
object FreeLook : Module() {
    val SENSITIVITY = register(SettingNumber("Sensitivity", 8f, 0.1f..10f))

    var pitch = 0.0f
    var yaw = 0F

    init {
        enableCallback {
            if (mc.player != null) {
                yaw = mc.player!!.renderYaw
                pitch = mc.player!!.renderPitch

                mc.options.perspective = Perspective.THIRD_PERSON_BACK
            }
        }

        disableCallback {
            mc.options.perspective = Perspective.FIRST_PERSON
        }
    }
}
