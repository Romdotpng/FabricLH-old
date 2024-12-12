package lavahack.client.features.subsystem.subsystems

import lavahack.client.LavaHack
import lavahack.client.event.events.WorldEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.interfaces.impl.receiveListener
import net.minecraft.block.Blocks
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket

/**
 * @author _kisman_
 * @since 12:09 of 07.08.2023
 */
object BlockUpdateListener : SubSystem(
    "Block Update Listener"
) {
    override fun init() {
        var prev1 : BlockUpdateS2CPacket? = null
        var prev2 : BlockUpdateS2CPacket? = null

        receiveListener {
            when(it.packet) {
                is BlockUpdateS2CPacket -> {
                    val pos = it.packet.pos
                    val state = it.packet.state
                    val block = state.block

                    if(block == Blocks.AIR && prev1 != null) {
                        prev1 = null
                        prev2 = null

                        LavaHack.EVENT_BUS.post(WorldEvent.BlockUpdate.Break(pos))

                        return@receiveListener
                    }

                    if(block != Blocks.AIR && prev1 != null && prev2 != null) {
                        prev1 = null
                        prev2 = null

                        LavaHack.EVENT_BUS.post(WorldEvent.BlockUpdate.Place(pos, state))

                        return@receiveListener
                    }

                    if(prev1 == null) {
                        prev1 = it.packet
                    } else if(prev2 == null) {
                        prev2 = it.packet
                    }
                }
            }
        }
    }
}