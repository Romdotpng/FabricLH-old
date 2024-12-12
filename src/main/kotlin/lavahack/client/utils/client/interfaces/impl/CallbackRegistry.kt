@file:Suppress("LocalVariableName")

package lavahack.client.utils.client.interfaces.impl

import lavahack.client.callback.Callback
import lavahack.client.utils.client.interfaces.ICallbackRegistry

/**
 * @author _kisman_
 * @since 14:46 of 21.06.2023
 */
class CallbackRegistry {
    val callbacks = mutableMapOf<Int, MutableList<Callback>>()

    fun add(
        callback : Callback
    ) {
        if(callbacks.contains(callback.index)) {
            callbacks[callback.index]!!.add(callback)
        } else {
            callbacks[callback.index] = mutableListOf(callback)
        }
    }

    fun addAll(
        callbacks : List<Callback>
    ) {
        for(callback in callbacks) {
            add(callback)
        }
    }

    fun addAll(
        index : Int,
        _callbacks : List<Callback>
    ) {
        if(callbacks.contains(index)) {
            callbacks[index]!!.addAll(_callbacks)
        } else {
            callbacks[index] = _callbacks.toMutableList()
        }
    }
    operator fun get(
        index : Int
    ) {
        for(callback in callbacks[index] ?: emptyList()) {
            callback.invoke()
        }
    }
}

fun ICallbackRegistry.register(
    callback : Callback
) {
    this.callbacks.add(callback)
}