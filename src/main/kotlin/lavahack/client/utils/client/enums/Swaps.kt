package lavahack.client.utils.client.enums

import lavahack.client.features.module.modules.exploit.PacketMine
import lavahack.client.utils.hotbar2inventory
import lavahack.client.utils.inventorySwap
import lavahack.client.utils.mc
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket

/**
 * @author _kisman_
 * @since 11:52 of 04.07.2023
 */
enum class Swaps(
    private val _pre : (Int) -> Unit,
    private val _post : (Int, Int) -> Unit
) {
    None({ }, { _, _ -> }),
    Normal(
        { current ->
            mc.networkHandler!!.sendPacket(UpdateSelectedSlotC2SPacket(current))
            mc.player!!.inventory.selectedSlot = current
        },
        { _, _ -> }
    ),
    Silent(
        { current ->
            mc.networkHandler!!.sendPacket(UpdateSelectedSlotC2SPacket(current))
            mc.player!!.inventory.selectedSlot = current
        },
        { _, prev ->
            mc.networkHandler!!.sendPacket(UpdateSelectedSlotC2SPacket(prev))
            mc.player!!.inventory.selectedSlot = prev
        }
    ),
    StrictSilent(
        { current ->
            inventorySwap(hotbar2inventory(current))
        },
        { current, _ ->
            inventorySwap(hotbar2inventory(current))
        }
    ),
    AdaptiveSilent(
        { current ->
            if(PacketMine.MINING) {
                StrictSilent._pre(current)
            } else {
                Silent._pre(current)
            }
        },
        { current, prev ->
            if(PacketMine.MINING) {
                StrictSilent._post(current, prev)
            } else {
                Silent._post(current, prev)
            }
        }
    ),
    Packet(
        { current ->
            mc.networkHandler!!.sendPacket(UpdateSelectedSlotC2SPacket(current))
        },
        { current, _ ->
            mc.networkHandler!!.sendPacket(UpdateSelectedSlotC2SPacket(current))
        }
    )

    ;

    private var current = -1
    private var prev = -1

    fun pre(
        slot : Int
    ) {
        if(slot != mc.player!!.inventory.selectedSlot) {
            current = slot
            prev = mc.player!!.inventory.selectedSlot
            _pre(slot)
        } else {
            prev = -1
        }
    }

    fun post() {
        if(prev != -1) {
            _post(current, prev)
            current = -1
            prev = -1
        }
    }
}