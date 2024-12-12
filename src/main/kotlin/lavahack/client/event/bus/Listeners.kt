package lavahack.client.event.bus

import lavahack.client.LavaHack

inline fun <reified T : Event> listener(
    priority : Int = 0,
    noinline block : (T) -> Unit
) {
    val listener = Listener(T::class.java, priority, block)

    LavaHack.EVENT_BUS.subscribe(listener)
}