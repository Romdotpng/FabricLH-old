package lavahack.client.features.module.modules.player

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.receiveListener
import lavahack.client.utils.client.interfaces.impl.register
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket

/**
 * @author _kisman_
 * @since 12:32 of 29.05.2023
 */
@Module.Info(
    name = "Velocity",
    aliases = "Anti-KnockBack",
    category = Module.Category.PLAYER
)
class Velocity : Module() {
    init {
        val explosions = register(Setting("Explosions", false))
        val knockback = register(Setting("Knockback", false))

        receiveListener {
            when(it.packet) {
                is ExplosionS2CPacket -> {
                    if(explosions.value) {
                        it.cancel()
                    }
                }

                is EntityVelocityUpdateS2CPacket -> {
                    if(knockback.value) {
                        val id = it.packet.id

                        if(id == mc.player!!.id) {
                            it.cancel()
                        }
                    }
                }
            }
        }
    }
}