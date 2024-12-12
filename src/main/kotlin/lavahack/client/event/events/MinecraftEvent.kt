package lavahack.client.event.events

import lavahack.client.event.bus.Event

open class MinecraftEvent : Event() {
    class GameRenderer {
        class Render {
            class End : MinecraftEvent()
        }
    }
}