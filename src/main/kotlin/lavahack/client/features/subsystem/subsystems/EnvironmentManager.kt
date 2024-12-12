package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.client.enums.Anticheats
import lavahack.client.utils.client.enums.Protocols
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.element

object EnvironmentManager : SubSystem(
    "Environment Manager"
) {
    private val PROTOCOL = register(SettingEnum("Protocol", Protocols.New))
    private val ANTICHEAT = register(SettingEnum("Anticheat", Anticheats.Vanilla))
    private val OVERRIDE_PROTOCOL = register(Setting("Override Protocol", false))
    private val OVERRIDE_ANTICHEAT = register(Setting("Override Anticheat", false))

    val STRICT_DIRECTION_LINKER = OVERRIDE_PROTOCOL to { ANTICHEAT.valEnum != Anticheats.Vanilla }
    val BLOCK_ROTATE_LINKER = OVERRIDE_PROTOCOL to { ANTICHEAT.valEnum.rotate.element() }
}