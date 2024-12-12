package lavahack.client.event.bus

/**
 * @author _kisman_
 * @since 10:10 of 08.05.2023
 */
@Suppress("UNCHECKED_CAST")
class Listener<E>(
    val type : Class<E>,
    val priority : Int = 0,
    private val invoker : (E) -> Unit
) {
    fun invoke(
        event : Any
    ) {
        invoker(event as E)
    }
}