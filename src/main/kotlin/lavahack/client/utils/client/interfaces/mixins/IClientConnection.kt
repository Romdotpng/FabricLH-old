package lavahack.client.utils.client.interfaces.mixins

import net.minecraft.network.packet.Packet

/**
 * @author _kisman_
 * @since 4:45 of 29.07.2023
 */
interface IClientConnection {
    fun sendNoEvent(
        packet : Packet<*>
    )
}