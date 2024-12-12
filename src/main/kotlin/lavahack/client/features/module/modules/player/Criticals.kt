package lavahack.client.features.module.modules.player

import lavahack.client.features.module.Module
import lavahack.client.features.module.modules.combat.Aura
import lavahack.client.mixins.AccessorPlayerInteractEntityC2SPacket
import lavahack.client.settings.Setting
import lavahack.client.utils.client.enums.InteractTypes
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.sendListener
import lavahack.client.utils.type
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

@Module.Info(
    name = "Criticals",
    description = "Makes every hit critical",
    category = Module.Category.PLAYER
)
class Criticals : Module() {
    init {
        val strict = register(Setting("Strict", false))
        val onlyWithAura = register(Setting("Only With Aura", false))
        val onlyPlayers = register(Setting("Only Players", true))

        sendListener {
            if(mc.player != null && mc.player!!.isOnGround && !mc.player!!.isInLava && !mc.player!!.isTouchingWater && !mc.player!!.isSubmergedInWater && (!onlyWithAura.value || Aura.state)) {
                when (it.packet) {
                    is PlayerInteractEntityC2SPacket -> {
                        val type = it.packet.type

                        if (type == InteractTypes.ATTACK) {
                            val id = (it.packet as AccessorPlayerInteractEntityC2SPacket).id
                            val entity = mc.world!!.getEntityById(id)

                            if (entity is LivingEntity && (!onlyPlayers.value || entity is PlayerEntity)) {
                                val x = mc.player!!.x
                                val y = mc.player!!.y
                                val z = mc.player!!.z

                                if (strict.value) {
                                    mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.07, z, false))
                                    mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.08, z, false))
                                    mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false))
                                }

                                mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.05, z, false))
                                mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false))
                                mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.012, z, false))
                                mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false))

                                mc.player!!.addCritParticles(entity)
                            }
                        }
                    }
                }
            }
        }
    }
}