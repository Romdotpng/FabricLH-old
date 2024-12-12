package lavahack.client.features.config.configs.features

import lavahack.client.features.hud.Huds
import lavahack.client.features.module.Module

/**
 * @author _kisman_
 * @since 22:06 of 24.05.2023
 */
@Module.Info(
    name = "Huds",
    messages = false,
    properties = Module.Properties(
        bind = false,
        visible = false
    )
)
object HudsConfig : AbstractModulesConfig(
    Huds.huds,
    Huds.names
)