package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.HoleProcessor
import lavahack.client.features.subsystem.subsystems.Targetable
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.item.Items
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

@Module.Info(
    name = "HoleFill",
    description = "Fills holes around you.",
    aliases = "HoleFiller",
    category = Module.Category.COMBAT,
    holeprocessor = true,
    targetable = Targetable(
        nearest = true,
        self = true
    )
)
class HoleFill : Module() {
    init {
        val pattern = register(BlockPlacerPattern(this, this))
        val sort = register(Setting("Sort", false))

        val smartGroup = register(SettingGroup("Smart"))
        val smartState = register(smartGroup.add(Setting("State", false)))
        val smartRaytrace = register(smartGroup.add(Setting("Raytrace", false)))

        smartGroup.prefix("Smart")

        selfAsEnemy = !smartState

        fun trace() : List<HitResult> {
            val results = mutableListOf<HitResult>()
            val box = mc.player!!.boundingBox
            val center = box.center

            val starts = mutableListOf(
                center,
                Vec3d(box.minX, center.y, box.minZ),
                Vec3d(box.minX, center.y, box.maxZ),
                Vec3d(box.maxX, center.y, box.minZ),
                Vec3d(box.maxX, center.y, box.maxZ)
            )

            for(start in starts) {
                val end = Vec3d(start.x, start.y - 1.0, start.z)
                val context = context(start, end)
                val result = mc.world!!.raycast(context)

                if(result != null) {
                    results.add(result)
                }
            }

            return results
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            val posses = mutableListOf<BlockPos>()

            if(smartState.value) {
                if(enemy != null) {
                    if(smartRaytrace.value || enemy!!.forwardSpeed != 0f || enemy!!.sidewaysSpeed != 0f) {
                        val results = trace()
                        val possible = mutableSetOf<BlockPos>()

                        for(result in results) {
                            if(result is BlockHitResult) {
                                val pos = result.blockPos!!

                                possible.add(pos)
                            }
                        }

                        for(pos in possible) {
                            if(HoleProcessor.holeBlocks.contains(pos) && placeable(pos)) {
                                posses.add(pos)
                            }
                        }
                    }
                }
            } else {
                for(pos in HoleProcessor.holeBlocks) {
                    if(placeable(pos)) {
                        posses.add(pos)
                    }
                }

                if(sort.value) {
                    posses.sortWith(Comparator.comparingDouble { it.vec() distanceSq mc.player!!.pos })
                }
            }

            pattern.place(posses, Items.OBSIDIAN)
        }
    }
}