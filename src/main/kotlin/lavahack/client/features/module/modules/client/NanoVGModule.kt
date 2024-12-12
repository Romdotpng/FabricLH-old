package lavahack.client.features.module.modules.client

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.register

@Module.Info(
    name = "NanoVG",
    description = "Enables NanoVG rendering",
    category = Module.Category.CLIENT
)
object NanoVGModule : Module() {
    val RECTS = register(Setting("Rects", false))
    val OUTLINE_RECTS = register(Setting("Outline Rects", false))
    val GRADIENT_RECTS = register(Setting("Gradient Rects", false))
}