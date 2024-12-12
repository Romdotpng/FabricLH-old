package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.HoleProcessor
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.impl.moveListener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.math.ALMOST_ZERO
import lavahack.client.utils.raycast
import lavahack.client.utils.velocityX
import lavahack.client.utils.velocityZ
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext

@Module.Info(
    name = "Anchor",
    description = "idfc",
    category = Module.Category.MOVEMENT,
    holeprocessor = true,
    beta = true,
)
class Anchor : Module() {
    init {
        val height = register(SettingNumber("Height", 4, 1..10))
        val pitch = register(SettingNumber("Pitch", -45, -90..90))

        fun trace() : Set<BlockPos> {
            val posses = hashSetOf<BlockPos>()
            val box = mc.player!!.boundingBox
            val center = box.center

            val starts = mutableListOf(
                Vec3d(box.minX + ALMOST_ZERO, center.y, box.minZ + ALMOST_ZERO),
                Vec3d(box.minX + ALMOST_ZERO, center.y, box.maxZ - ALMOST_ZERO),
                Vec3d(box.maxX - ALMOST_ZERO, center.y, box.minZ + ALMOST_ZERO),
                Vec3d(box.maxX - ALMOST_ZERO, center.y, box.maxZ - ALMOST_ZERO)
            )

            for(start in starts) {
                val end = start.add(0.0, -height.value.toDouble(), 0.0)
                val result = raycast(start, end)

                if(result is BlockHitResult) {
                    val pos = result.blockPos.up()

                    posses.add(pos)
                }
            }

            return posses
        }

        moveListener {
            if(mc.player == null || mc.world == null) {
                return@moveListener
            }

            val posses = trace()

            println(posses.joinToString())

            if(HoleProcessor.holeBlocks.containsAll(posses)) {
                println("stopping movement")
                //TODO: centring
                it.x = 0.0//it.x.toDouble() * 0.01
                it.z = 0.0//it.x.toDouble() * 0.01
            }

            /*val pos = trace()?.up()

            println(pos)

            if(HoleProcessor.holeBlocks.contains(pos)) {
                println("stopping movement")
                it.x = it.x.toDouble() * 0.05
                it.y = it.x.toDouble() * 0.05
                *//*mc.player!!.velocityX = 0.0
                mc.player!!.velocityZ = 0.0*//*
            }*/
        }
    }
}