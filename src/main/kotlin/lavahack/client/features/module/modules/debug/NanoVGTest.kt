package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.screenListener
import lavahack.client.utils.render.screen.*
import java.awt.Color

@Module.Info(
    name = "NanoVGTest",
    description = "Test of implementation of NanoVG",
    category = Module.Category.DEBUG
)
class NanoVGTest : Module() {
    init {
        val text = register(Setting("Text", true))
        val radius = register(SettingNumber("Radius", 1f, 0.1f..20f))
        val size = register(SettingNumber("Size", 12f, 10f..50f))

        screenListener {
            prepareVG()

            roundedRectWH(
                100,
                100,
                200,
                100,
                Color(255, 0, 0, 120),
                radius.value
            )

            if(text.value) {
                text("hello world", 100, 100, Color(255, 255, 255, 120), "lexenddeca-regular", size.value)
            }

            releaseVG()
        }
    }
}