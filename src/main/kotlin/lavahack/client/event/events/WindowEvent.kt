package lavahack.client.event.events

import lavahack.client.event.bus.Event

open class WindowEvent : Event() {
    class Resize : WindowEvent()
}