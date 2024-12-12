package lavahack.client.callback

import lavahack.client.utils.Stopwatch

/**
 * @author _kisman_
 * @since 14:40 of 21.06.2023
 */
open class Callback(
    val index : Int,
    val invoker : () -> Unit
) {
    fun invoke() {
        invoker()
    }

    class Delayed(
        index : Int,
        invoker : () -> Unit
    ) : Callback(
        index,
        invoker
    ) {
        val stopwatch = Stopwatch()
    }
}