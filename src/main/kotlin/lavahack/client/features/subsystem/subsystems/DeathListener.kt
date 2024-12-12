package lavahack.client.features.subsystem.subsystems

import lavahack.client.LavaHack
import lavahack.client.event.events.WorldEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.mc
import net.minecraft.entity.player.PlayerEntity

object DeathListener : SubSystem(
    "Death Listener"
) {
    val deaths = mutableSetOf<PlayerEntity>()

    override fun init() {
        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            for(entity in mc.world!!.entities.toList()) {
                if(entity is PlayerEntity) {
                    if(entity.isAlive) {
                        if(deaths.contains(entity)) {
                            deaths.remove(entity)
                        }
                    } else if(!deaths.contains(entity)) {
                        deaths.add(entity)

                        LavaHack.EVENT_BUS.post(WorldEvent.Death(entity))
                    }
                }
            }
        }
    }
}