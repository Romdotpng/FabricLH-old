package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.CRYSTALS_PER_SECOND
import net.minecraft.text.Text

@Module.Info(
    name = "CrystalsPerSecond",
    display = "Crystals/S"
)
class CrystalsPerSecond : Hud.Single(
    { Text.literal("Crystals/S") },
    { Text.literal("$CRYSTALS_PER_SECOND") },
    ": "
)