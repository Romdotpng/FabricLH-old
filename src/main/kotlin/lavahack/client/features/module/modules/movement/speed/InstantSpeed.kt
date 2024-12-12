package lavahack.client.features.module.modules.movement.speed

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.moveListener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.strafe
import net.minecraft.entity.MovementType
import kotlin.math.max

/**
 * @author _kisman_
 * @since 18:43 of 31.05.2023
 */
@Module.Info(
    name = "InstantSpeed",
    display = "Instant",
    description = "20km/h everytime!",
    submodule = true
)
class InstantSpeed : Module() {
    init {
        val liquids = register(Setting("Liquids", false))
        val sneak = register(Setting("Sneak", false))
        val overwrite = register(Setting("Overwrite", false))

        moveListener(-1) {
            if(it.type != MovementType.SELF || (!liquids.value && (mc.player!!.isSubmergedInWater || mc.player!!.isTouchingWater || mc.player!!.isInLava)) || (!sneak.value && mc.player!!.isSneaking)) {
                return@moveListener
            }

            val motions = strafe()

            if(overwrite.value) {
                it.x = max(it.x.toDouble(), motions[0])
                it.z = max(it.z.toDouble() , motions[1])
            } else {
                it.x = motions[0]
                it.z = motions[1]
            }

            it.cancel()
        }
    }
}