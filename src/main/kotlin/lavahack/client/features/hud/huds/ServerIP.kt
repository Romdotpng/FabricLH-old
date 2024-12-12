package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.utils.currentServerIP
import net.minecraft.text.Text


@Module.Info(
    name = "Server",
    description = "Displays the server you are currently playing on"
)
class ServerIP : Hud.Single(
    { Text.literal("Server") },
    { Text.literal(currentServerIP()) }
)