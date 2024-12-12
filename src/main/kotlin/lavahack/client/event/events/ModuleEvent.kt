package lavahack.client.event.events

import lavahack.client.event.bus.Event
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

open class ModuleEvent : Event() {
    class PacketMine {
        class ClickBlock(val pos : BlockPos, val direction : Direction, val allowPreplace : Boolean) : ModuleEvent()
    }
}