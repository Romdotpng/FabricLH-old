package lavahack.client.features.subsystem.subsystems

import lavahack.client.LavaHack
import lavahack.client.event.events.WorldEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.interfaces.impl.receiveListener
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.mc
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket

/**
 * @author _kisman_ && Kuro_Here
 * @since 20:36 of 03.01.2024
 */
object PopListener : SubSystem("Pop Listener") {
    val pops = mutableMapOf<PlayerEntity, Int>()

    override fun init() {
        receiveListener {
            when(it.packet) {
                is EntityStatusS2CPacket -> {
                    val status = it.packet.status

                    if(status == EntityStatuses.USE_TOTEM_OF_UNDYING) {
                        val entity = it.packet.getEntity(mc.world)

                        if(entity is PlayerEntity) {
                            pops[entity] = pops.getOrDefault(entity, 0) + 1

                            LavaHack.EVENT_BUS.post(WorldEvent.Pop(entity))
                        }
                    }
                }
            }
        }

        tickListener {
            val deadPlayers = mc.world!!.players.filter { it.health <= 0 }
            deadPlayers.forEach { pops.remove(it) }
        }
    }
}