package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Stopwatch
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.mc
import lavahack.client.utils.sphere
import lavahack.client.utils.world.Hole
import lavahack.client.utils.world.hole
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 13:52 of 28.07.2023
 */
object HoleProcessor : SubSystem(
    "Hole Processor"
) {
    private val RANGE = register(SettingNumber("Range", 5, 0..20))
    private val UPDATE_DELAY = register(SettingNumber("Update Delay", 100L, 0L..1000L))

    val states = mutableListOf<() -> Boolean>()

    val holeBlocks = mutableSetOf<BlockPos>()
    val holes = mutableSetOf<Hole>()

    override fun init() {
        val stopwatch = Stopwatch()

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            if(stopwatch.passed(UPDATE_DELAY.value, true)) {
                holeBlocks.clear()
                holes.clear()

                for(state in states) {
                    if(state()) {
                        sphere(mc.player!!.blockPos, RANGE.value) {
                            val hole = hole(it)

                            if(hole != null) {
                                holeBlocks.addAll(hole.posses)
                                holes.add(hole)
                            }
                        }

                        return@tickListener
                    }
                }
            }
        }
    }
}