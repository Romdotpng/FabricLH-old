package lavahack.client.features.hud.huds

import lavahack.client.LavaHack
import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import net.minecraft.text.Text

/**
 * @author _kisman_
 * @since 19:21 of 08.05.2023
 */
@Module.Info(
    name = "Watermark",
    description = "Watermark of the client",
    state = true
)
class Watermark : Hud.Single(
    { Text.literal("LavaHack") },
    { Text.literal(LavaHack.VERSION) },
    " "
)