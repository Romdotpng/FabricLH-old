package lavahack.client.utils.client.interfaces.impl

import lavahack.client.LavaHack
import lavahack.client.event.bus.Event
import lavahack.client.event.bus.Listener
import lavahack.client.event.events.*
import lavahack.client.utils.client.interfaces.IListenerRegistry
import java.lang.AssertionError

/**
 * @author _kisman_
 * @since 8:52 of 26.06.2023
 */
class ListenerRegistry {
    val listeners = mutableListOf<Listener<*>>()

    fun subscribe() {
        for(listener in listeners) {
            LavaHack.EVENT_BUS.subscribe(listener)
        }
    }

    fun unsubscribe() {
        for(listener in listeners) {
            LavaHack.EVENT_BUS.unsubscribe(listener)
        }
    }
}

fun IListenerRegistry.listeners(
    vararg listeners : Listener<*>
) {
    this.listeners.listeners.addAll(listeners)
}

inline fun <reified T : Event> IListenerRegistry.listener(
    priority : Int = 1,
    noinline block : (T) -> Unit
) {
    val listener = Listener(T::class.java, priority) {
        try {
            block(it)
        } catch(_ : AssertionError) { }
    }

    this.listeners(listener)
}

fun IListenerRegistry.tickListener(
    priority : Int = 1,
    block : (TickEvent.Pre) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.screenListener(
    priority : Int = 1,
    block : (Render2DEvent.Pre) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.worldListener(
    priority : Int = 1,
    block : (Render3DEvent.Post) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.moveListener(
    priority : Int = 1,
    block : (PlayerEvent.Move) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.entityAddListener(
    priority : Int = 1,
    block : (WorldEvent.Add) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.entityRemoveListener(
    priority : Int = 1,
    block : (WorldEvent.Remove) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.sendListener(
    priority : Int = 1,
    block : (PacketEvent.Send) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.receiveListener(
    priority : Int = 1,
    block : (PacketEvent.Receive) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.placeListener(
    priority : Int = 1,
    block : (WorldEvent.BlockUpdate.Place) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.breakListener(
    priority : Int = 1,
    block : (WorldEvent.BlockUpdate.Break) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.popListener(
    priority : Int = 1,
    block : (WorldEvent.Pop) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.deathListener(
    priority : Int = 1,
    block : (WorldEvent.Death) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.motionPreListener(
    priority : Int = 1,
    block : (PlayerEvent.Motion.Pre) -> Unit
) {
    listener(priority, block)
}

fun IListenerRegistry.motionPostListener(
    priority : Int = 1,
    block : (PlayerEvent.Motion.Post) -> Unit
) {
    listener(priority, block)
}