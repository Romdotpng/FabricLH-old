package lavahack.client.features.module.modules.render

import lavahack.client.features.module.Module
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener

/**
 * @author _kisman_
 * @since 13:50 of 12.05.2023
 */
@Module.Info(
    name = "FovModifier",
    description = "Allows you to change your fov",
    category = Module.Category.WIP
)
class FovModifier : Module() {
    init {
        val fov = register(SettingNumber("Fov", 110, 30..170))

        tickListener {
            mc.options.fov.value = fov.value
        }
    }
}