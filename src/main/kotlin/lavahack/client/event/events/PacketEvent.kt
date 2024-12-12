package lavahack.client.event.events

import lavahack.client.event.bus.Event
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet

/**
 * @author _kisman_
 * @since 1:19 of 29.05.2023
 */
open class PacketEvent : Event() {
    class Send(val packet : Packet<*>) : PacketEvent() {
        class Movement {
            class Pre : PacketEvent()
            class Post : PacketEvent()
        }
    }

    class Receive(val packet : Packet<out PacketListener>, val listener : PacketListener) : PacketEvent()
}