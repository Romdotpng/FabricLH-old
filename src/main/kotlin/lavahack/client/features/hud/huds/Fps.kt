package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.utils.mc
import net.minecraft.text.Text

/**
 * @author _kisman_
 * @since 14:09 of 12.05.2023
 */
@Module.Info(
    name = "FPS",
    description = "Shows current FPS"
)
class Fps : Hud.Single(
    { Text.literal("FPS") },
    { Text.literal(mc.currentFps.toString()) },
    ": "
)