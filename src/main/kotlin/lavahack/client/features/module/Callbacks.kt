package lavahack.client.features.module

import lavahack.client.callback.Callback
import lavahack.client.utils.client.interfaces.ICallbackRegistry
import lavahack.client.utils.client.interfaces.impl.register

/**
 * TODO: reset callback
 *
 * @author _kisman_
 * @since 14:54 of 21.06.2023
 */

fun ICallbackRegistry.enableCallback(
    block : () -> Unit
) {
    register(Callback(0, block))
}

fun ICallbackRegistry.disableCallback(
    block : () -> Unit
) {
    register(Callback(1, block))
}

fun ICallbackRegistry.threadCallback(
    block : () -> Unit
) {
    register(Callback.Delayed(2, block))
}

//TODO: reset callback