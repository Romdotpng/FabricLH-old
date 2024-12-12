package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.impl.moveListener
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.entity.MovementType
import org.joml.Vector2d

@Module.Info(
    name = "AutoMoveTest",
    description = "Tests automatic movement",
    category = Module.Category.DEBUG
)
class AutoMoveTest : Module() {
    init {
        var end : Vector2d? = null

        enableCallback {
            if(mc.player == null || mc.world == null) {
                return@enableCallback
            }

            end = mc.player!!.blockPos!!.offset(mc.player!!.horizontalFacing).offset(mc.player!!.horizontalFacing.right).vec().xz

            println(end)
        }

        tickListener {
            if(mc.player == null || mc.world == null || end == null) {
                return@tickListener
            }

            val motions = move(mc.player!!.pos.xz, end!!, SPRINTING_SPEED)
            val motionX = motions.first
            val motionZ = motions.second

            mc.player!!.setPosition(mc.player!!.pos.add(motionX, 0.0, motionZ))
        }

        moveListener {
            if(end == null || it.type != MovementType.SELF) {
                return@moveListener
            }

            /*val motions = move(mc.player!!.pos.xz, end!!, SPRINTING_SPEED)
            val motionX = motions.first
            val motionZ = motions.second

            it.x = motionX
            it.y = 0.0
            it.z = motionZ

            mc.player!!.noClip = true
            it.cancel()*/

//            println("$motionX $motionZ")
        }
    }
}