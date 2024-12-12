package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.moveListener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.math.sqrt
import lavahack.client.utils.velocityX
import lavahack.client.utils.velocityZ

@Module.Info(
    name = "MotionLogger",
    description = "Stuff i made to debug future client",
    category = Module.Category.DEBUG
)
class MotionLogger : Module() {
    init {
//        val whenOnGround = register(Setting("When On Ground", false))
//        val whenInAir = register(Setting("When In Air", false))
        val flag = register(Setting("Flag", false))

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            val velocityX = mc.player!!.velocityX
//            val velocityY = mc.player!!.velocityY
            val velocityZ = mc.player!!.velocityZ

            val velocity = sqrt(velocityX * velocityX + velocityZ * velocityZ)

            println(velocity)

            /*var log = false

            if(mc.player!!.isOnGround) {
                if(whenOnGround.value) {
                    log = true
                }
            } else if(whenInAir.value) {
                log = true
            }

            if(log) {
                println(mc.player!!.velocity)
            }*/
        }

        moveListener {

        }
    }
}