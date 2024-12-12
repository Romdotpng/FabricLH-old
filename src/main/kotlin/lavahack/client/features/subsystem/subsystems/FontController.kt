package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.enums.Fonts
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.ranges.step

object FontController : SubSystem(
    "Font Controller"
) {
    val FONT = register(SettingEnum("Font", Fonts.Vanilla))
    var OFFSET = 1f

    //TODO: change to init() method
    init {
        register(SettingNumber("Shadow Offset", 1f, 0f..1f step 0.1f) { OFFSET = it.value })
    }
}