package lavahack.client.features.config.configs.features

import lavahack.client.features.module.Module
import lavahack.client.features.module.Modules

/**
 * @author _kisman_
 * @since 13:54 of 21.05.2023
 */
@Module.Info(
    name = "Modules",
    messages = false,
    properties = Module.Properties(
        bind = false,
        visible = false
    )
)
object ModulesConfig : AbstractModulesConfig(
    Modules.modules,
    Modules.names
)