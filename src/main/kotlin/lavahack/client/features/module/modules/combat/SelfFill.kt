package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.client.ranges.step
import lavahack.client.utils.placeBlock
import lavahack.client.utils.placeableSide
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.BlockPos

/**
 * @author Cubic
 * @since 19.08.2023
 */
@Module.Info(
    name = "SelfFill",
    description = "Glitches you into an obsidian block",
    aliases = "Burrow, BlockLag",
    category = Module.Category.WIP,
    beta = true
)
class SelfFill : Module() {

    val offset = register(SettingNumber("Offset", 4.0, -10.0..10.0 step 0.1))
    val rotate = register(Setting("Rotate", false))
    val packet = register(Setting("Packet", true))
    val swing = register(Setting("Swing", true))

    init {

        fun fakeJump() {
            val x = mc.player!!.pos.x
            val y = mc.player!!.pos.y
            val z = mc.player!!.pos.z
            mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.41999998688698, z, true))
            mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.7531999805211997, z, true))
            mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.00133597911214, z, true))
            mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.16610926093821, z, true))
            mc.player!!.setPos(x, y + 1.16610926093821, z)
        }

        tickListener {

            if (mc.player == null || mc.world == null) {
                return@tickListener
            }

            val playerPos = BlockPos(mc.player!!.pos.x.toInt(), mc.player!!.pos.y.toInt(), mc.player!!.pos.x.toInt())

            if (mc.world!!.getBlockState(playerPos).block != Blocks.AIR) {
                return@tickListener
            }

            if (/*!placeable(playerPos, mc.player!!) ||*/ placeableSide(playerPos) == null) {
                return@tickListener
            }

            val slot = InventoryLocations.Hotbar.findInventoryItem(Items.OBSIDIAN)

            if (slot == -1) {
                return@tickListener
            }

            fakeJump()

            placeBlock(playerPos, Items.OBSIDIAN, packet = packet.value, swing = swing.value)

            if (true) {
                mc.player!!.setPos(mc.player!!.pos.x, mc.player!!.pos.y - 1.16610926093821, mc.player!!.pos.z)

                val off = offset.value
                mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player!!.pos.x, mc.player!!.pos.y + off, mc.player!!.pos.z, false))
            }

            toggle()

        }
    }
}