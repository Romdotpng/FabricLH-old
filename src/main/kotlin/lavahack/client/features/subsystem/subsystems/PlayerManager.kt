package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.enums.SpeedUnits
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.mc
import kotlin.math.sqrt

/**
 * @author _kisman_
 * @since 17:56 of 31.05.2023
 */
object PlayerManager : SubSystem(
    "Player Manager"
) {
    var distance = -1.0

    override fun init() {
        tickListener {
            if (mc.player == null || mc.world == null) {
                return@tickListener
            }

            val deltaX = mc.player!!.x - mc.player!!.prevX
            val deltaZ = mc.player!!.z - mc.player!!.prevZ

            distance = sqrt(deltaX * deltaX + deltaZ * deltaZ)
        }
    }
}

fun speed(
    unit : SpeedUnits
) = unit.modify(PlayerManager.distance)