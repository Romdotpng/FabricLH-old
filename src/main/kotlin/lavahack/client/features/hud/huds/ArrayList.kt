package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.features.module.Modules
import lavahack.client.features.subsystem.subsystems.GRAY
import lavahack.client.features.subsystem.subsystems.colored
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.register
import net.minecraft.text.Text

/**
 * @author _kisman_
 * @since 11:26 of 05.07.2023
 */
@Suppress("PrivatePropertyName")
@Module.Info(
    name = "ArrayList",
    description = "Shows list of enabled features"
)
class ArrayList : Hud.Multi(
    true
) {
    private val INFOS = register(Setting("Infos", true))
    private val ONLY_BOUND = register(Setting("Only Bound", false))

    override fun elements(
        list : MutableList<Entry>
    ) {
        for(module in Modules.modules) {
            if(module.visible) {
                val text = Text.literal("").append(module.name)

                if(INFOS.value) {
                    val info = module.displayInfo()

                    if (!info.isNullOrEmpty()) {
                        text.append(" ").append(colored(info, GRAY))
                    }
                }

                list.add(Entry(module, text) { module.state && module.visible && (!ONLY_BOUND.value || module.keyboardKey != -1 || module.mouseButton != -1) })
            }
        }
    }
}