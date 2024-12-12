package lavahack.client.utils

/**
 * @author _kisman_
 * @since 18:01 of 31.05.2023
 */
class Stopwatch {
    var timestamp = System.currentTimeMillis()

    fun reset() {
        timestamp = System.currentTimeMillis()
    }

    fun passed(
        millis : Number,
        reset : Boolean = false
    ) = (System.currentTimeMillis() - timestamp >= millis.toLong()).also {
        if(it && reset) {
            reset()
        }
    }
}