package lavahack.client.event.bus

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 10:08 of 08.05.2023
 */
class EventBus {
    private val listeners = mutableMapOf<Class<*>, HashSet<Listener<*>>>()

    fun <T : Event> post(
        event : T
    ) : T {
        for(listener in (listeners[event.javaClass] ?: emptyList()).toMutableList()) {
            listener.invoke(event)
        }

        return event
    }

    fun <T : Event> post(
        event : T,
        ci : CallbackInfo,
        canceller : (T) -> Unit = { }
    ) = post(event).also {
        if(it.cancelled) {
            canceller(it)
            ci.cancel()
        }
    }

    fun subscribe(
        listener : Listener<*>
    ) {
        if(listeners.contains(listener.type)) {
            listeners[listener.type]!!.add(listener)
            listeners[listener.type] = hashSetOf(*listeners[listener.type]!!.sortedWith(Comparator.comparingInt { -it.priority }).toTypedArray())
        } else {
            listeners[listener.type] = hashSetOf(listener)
        }
    }

    fun unsubscribe(
        listener : Listener<*>
    ) {
        if(listeners.contains(listener.type)) {
            listeners[listener.type]!!.remove(listener)
        }
    }
}