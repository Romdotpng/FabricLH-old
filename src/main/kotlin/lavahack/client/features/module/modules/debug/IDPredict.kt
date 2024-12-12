package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.entityAddListener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.sendListener
import lavahack.client.utils.debug
import lavahack.client.utils.entity.EntityID
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket

/**
 * @author _kisman_
 * @since 22:40 of 03.08.2023
 */
@Module.Info(
    name = "IDPredict",
    description = "Debug module to test ID prediction",
    category = Module.Category.DEBUG
)
class IDPredict : Module() {
    init {
        val ping = register(Setting("Ping", false))
        val adaptive = register(Setting("Adaptive", false))

        var offset = 0
        var predicted = -1

        sendListener {
            when(it.packet) {
                is PlayerInteractBlockC2SPacket -> {
                    var maxId = Int.MIN_VALUE

                    for(entity in mc.world!!.entities) {
                        if(entity.id > maxId) {
                            maxId = entity.id
                        }
                    }

                    predicted = maxId

                    if(ping.value) {
                        val latency = mc.networkHandler!!.getPlayerListEntry(mc.networkHandler!!.profile.id)!!.latency / 50

                        for(i in latency..(latency + 10)) {
                            debug("id predict ${maxId + i} adaptive ${maxId + i + offset} | ${System.currentTimeMillis()}")
//                            mc.networkHandler!!.sendPacket(PlayerInteractEntityC2SPacket.attack(EntityID(maxId + 10), mc.player!!.isSneaking))
                        }
                    } else {
                        debug("id predict $maxId adaptive ${maxId + offset} | ${System.currentTimeMillis()}")
//                        mc.networkHandler!!.sendPacket(PlayerInteractEntityC2SPacket.attack(EntityID(maxId), mc.player!!.isSneaking))
                    }
                }
            }
        }

        entityAddListener {
            debug("entity add ${it.entity.id} | ${System.currentTimeMillis()}")

            offset = if(adaptive.value) {
                it.entity.id - predicted
            } else {
                0
            }
        }
    }
}