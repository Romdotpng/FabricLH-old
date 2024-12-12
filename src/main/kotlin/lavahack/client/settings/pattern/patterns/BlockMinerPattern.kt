package lavahack.client.settings.pattern.patterns

import lavahack.client.LavaHack
import lavahack.client.event.events.ModuleEvent
import lavahack.client.settings.pattern.Pattern
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.client.enums.BlockMinerLogics
import lavahack.client.utils.client.interfaces.impl.register
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

@Suppress("PrivatePropertyName")
class BlockMinerPattern : Pattern() {
    private val LOGIC = register(SettingEnum("Logic", BlockMinerLogics.Packet))

    private var prev : BlockPos? = null
    private var clicked = false

    fun mine(
        pos : BlockPos
    ) {
        if(pos != prev) {
            clicked = false
        }

        if(!clicked) {
            LavaHack.EVENT_BUS.post(ModuleEvent.PacketMine.ClickBlock(pos, Direction.DOWN, false))
            clicked = true
        }

        prev = pos
    }

    fun reset() {
        prev = null
        clicked = false
    }
}