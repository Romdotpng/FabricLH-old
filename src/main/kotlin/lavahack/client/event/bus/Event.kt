package lavahack.client.event.bus

/**
 * @author _kisman_
 * @since 10:09 of 08.05.2023
 */
abstract class Event {
    var cancelled = false

    fun cancel() {
        cancelled = true
    }
}