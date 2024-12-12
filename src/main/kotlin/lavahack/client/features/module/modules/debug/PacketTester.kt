package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.debug
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.BundleS2CPacket
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket

/**
 * @author _kisman_
 * @since 14:11 of 03.08.2023
 */
@Module.Info(
    name = "PacketTester",
    description = "Debug module to test some packet stuff",
    category = Module.Category.DEBUG
)
class PacketTester : Module() {
    init {
        receiveListener {
//            println(it.packet.javaClass.simpleName)

            when(it.packet) {
                is EntitiesDestroyS2CPacket -> {
                    for(id in it.packet.entityIds) {
                        val entity = mc.world!!.getEntityById(id)

                        if(entity is EndCrystalEntity) {
                            debug("entity destroy ${entity.pos} ${System.currentTimeMillis()}")
                        }
                    }
                }

                is EntitySpawnS2CPacket -> {
                    val entity = mc.world!!.getEntityById(it.packet.id)

                    if(entity is EndCrystalEntity) {
                        debug("entity spawn ${entity.pos} ${System.currentTimeMillis()}")
                    }
                }

                is BundleS2CPacket -> {
                    for(packet in it.packet.packets) {
//                        println("${packet.javaClass.simpleName} (bundle)")

                        when(packet) {
                            is EntitySpawnS2CPacket -> {
//                                val entity = mc.world!!.getEntityById(packet.id)
                                val id = packet.id
                                val type = packet.entityType

                                if(type == EntityType.END_CRYSTAL) {
                                    debug("entity spawn $id ${System.currentTimeMillis()} (bundle)")
                                }
                            }
                        }
                    }
                }

                is BlockEventS2CPacket -> {
                    debug("block event ${it.packet.pos} ${it.packet.block} ${it.packet.type} ${it.packet.data}")
                }

                is BlockUpdateS2CPacket -> {
                    debug("block update ${it.packet.pos} ${it.packet.state.block}")
                }

                /*is ExplosionS2CPacket -> {
                    println("explosion ${it.packet.x} ${it.packet.x} ${it.packet.x}")
                }*/
            }
        }

        entityAddListener {
            debug("entity add ${it.entity.id} ${System.currentTimeMillis()}")
        }

        entityRemoveListener {
            debug("entity remove ${it.entity.pos} ${System.currentTimeMillis()}")
        }

        placeListener {
            debug("block place ${it.pos} ${it.state.block}")
        }

        breakListener {
            debug("block break ${it.pos}")
        }
    }
}