package lavahack.client.features.module.modules.client

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.selectionbar.SelectionBar
import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.utils.client.interfaces.impl.register
import org.lwjgl.glfw.GLFW

/**
 * @author _kisman_
 * @since 10:19 of 11.05.2023
 */
@Module.Info(
    name = "Gui",
    display = "UI",
    description = "Opens UI",
    category = Module.Category.CLIENT,
    key = GLFW.GLFW_KEY_RIGHT_SHIFT,
    messages = false,
    properties = Module.Properties(
        visible = false
    )
)
class UIModule : Module() {
    init {
        register(ModuleGui)

        enableCallback {
            if(mc.player == null || mc.world == null) {
                state = false

                return@enableCallback
            }

            mc.setScreen(SelectionBar)

            state = false
        }
    }
}