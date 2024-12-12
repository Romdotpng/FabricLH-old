package lavahack.client.event.events

import lavahack.client.event.bus.Event
import lavahack.client.utils.mc
import net.minecraft.entity.MovementType

/**
 * @author _kisman_
 * @since 18:45 of 31.05.2023
 */
open class PlayerEvent : Event() {
    class Move(
        var type : MovementType,
        var x : Number,
        var y : Number,
        var z : Number
    ) : PlayerEvent()

    class Motion {
        class Pre : PlayerEvent() {
            var spoofing = false

            /*var x = mc.player!!.x ; set(value) { field = value ; spoofing = true }
            var y = mc.player!!.y ; set(value) { field = value ; spoofing = true }
            var z = mc.player!!.z ; set(value) { field = value ; spoofing = true }*/

            var yaw = mc.player!!.yaw ; set(value) { field = value ; spoofing = true }
            var pitch = mc.player!!.pitch ; set(value) { field = value ; spoofing = true }

            /*var prevYaw = mc.player!!.prevYaw ; set(value) { field = value ; spoofing = true }
            var prevPitch = mc.player!!.prevPitch ; set(value) { field = value ; spoofing = true }*/

            companion object {
                var IS_IN = false
            }
        }
        class Post : PlayerEvent()

        companion object {
            var IS_IN = false
        }
    }

    class Jump(
        val self : Boolean
    ) : PlayerEvent()
}