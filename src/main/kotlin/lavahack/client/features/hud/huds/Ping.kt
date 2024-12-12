package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.utils.ping
import net.minecraft.text.Text

@Module.Info(
    name = "Ping",
    description = "Shows current ping"
)
class Ping : Hud.Single(
    { Text.literal("Ping") },
    { Text.literal("${ping()}") },
    ": "
)