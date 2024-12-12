package lavahack.client.features.module.modules.misc

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.features.module.enableCallback
import lavahack.client.mixins.InvokerGameRenderer
import lavahack.client.settings.Setting
import lavahack.client.utils.SUPER_SECRET_SETTING_PROGRAMS
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.superSecretPrograms

@Module.Info(
    name = "SuperSecret",
    category = Module.Category.MISC
)
class SuperSecret : Module() {
    init {
        val secret = register(Setting("Secret", superSecretPrograms))
        val onlyGui = register(Setting("Only Gui", true))

        enableCallback {
            if(!onlyGui.value || mc.currentScreen != null) {
                (mc.gameRenderer as InvokerGameRenderer).loadPostProcessor0(SUPER_SECRET_SETTING_PROGRAMS[secret.value.current.index])
            }
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            if(!onlyGui.value || mc.currentScreen != null) {
                (mc.gameRenderer as InvokerGameRenderer).loadPostProcessor0(SUPER_SECRET_SETTING_PROGRAMS[secret.value.current.index])
            } else {
                mc.gameRenderer.disablePostProcessor()
            }
        }

        disableCallback {
            mc.gameRenderer.disablePostProcessor()
        }
    }
}