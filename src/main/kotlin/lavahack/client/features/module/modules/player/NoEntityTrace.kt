package lavahack.client.features.module.modules.player

import lavahack.client.features.module.Module

/**
 * @author _kisman_
 * @since 15:54 of 15.07.2023
 */
@Module.Info(
    name = "NoEntityTrace",
    description = "Ignores entities over crosshair",
    category = Module.Category.PLAYER
)
object NoEntityTrace : Module()