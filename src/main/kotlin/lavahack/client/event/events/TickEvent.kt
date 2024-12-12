package lavahack.client.event.events

import lavahack.client.event.bus.Event

/**
 * @author _kisman_
 * @since 10:33 of 08.05.2023
 */
open class TickEvent : Event() {
    class Pre : TickEvent()
    class Post : TickEvent()
}