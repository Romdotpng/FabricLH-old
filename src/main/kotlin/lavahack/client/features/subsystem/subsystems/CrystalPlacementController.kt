package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.enums.Protocols
import lavahack.client.utils.client.enums.Rotates
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 10:43 of 24.06.2023
 */
object CrystalPlacementController : SubSystem(
    "Crystal Placement Controller"
) {
    val PROTOCOL = register(SettingEnum("Protocol", Protocols.New))
    val LIQUID_PLACE = register(Setting("Liquid Place", false))
    val FIRE_PLACE = register(Setting("Fire Place", true))
    val MULTIPLACE = register(Setting("Multiplace", false))
    val NCP_CHECK = register(Setting("NCP Check", false))
    val STRICT_CHECK = register(Setting("Strict Check", false))
    val ENTITY_CHECK = register(Setting("Entity Check", true))
    val STRICT_DIRECTION = register(Setting("Strict Direction", false))

    private val ROTATES_GROUP = register(SettingGroup("Rotates"))
    val PLACE_ROTATE = register(ROTATES_GROUP.add(SettingEnum("Place Rotate", Rotates.None, "Place")))
    val BREAK_ROTATE = register(ROTATES_GROUP.add(SettingEnum("Break Rotate", Rotates.None, "Break")))
}