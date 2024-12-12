package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.utils.mc
import net.minecraft.text.Text

/**
 * @author _kisman_
 * @since 20:48 of 12.05.2023
 */
@Module.Info(
    name = "Welcomer",
    description = "hello"
)
class Welcomer : Hud.Single(
    { Text.literal("Welcome, ").append(mc.player!!.name) },
    { Text.literal("") }
)