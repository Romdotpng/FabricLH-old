package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.speed
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.client.enums.SpeedUnits
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.math.roundToPlace
import net.minecraft.text.Text

/**
 * @author _kisman_
 * @since 17:53 of 31.05.2023
 */

private val units = SettingEnum("Unit", SpeedUnits.KMH)

@Module.Info(
    name = "Speedometer",
    description = "Shows your moving speed"
)
class Speedometer : Hud.Single(
    { Text.literal("Speed") },
    { Text.literal("${roundToPlace(speed(units.valEnum), 1)} ${units.valEnum.display}") },
    ": "
) {
    init {
        register(units)
    }
}