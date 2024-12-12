package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.register

object DevelopmentSettings : SubSystem(
    "Development Settings"
) {
    val DEBUG_LOGS = register(Setting("Debug Logs", false))
    val FIX_NULLABLE_ENTITIES = /*Setting.Fast*/(register(Setting("Fix Nullable Entities", true)))
    val GL_DEBUG_LINES = register(Setting("GL_DEBUG_LINES", false))
    val SHADER_WARNS = Setting.Fast(register(Setting("Shader Warns", false)))
}