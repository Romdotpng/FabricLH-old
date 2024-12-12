package lavahack.client.utils.client.ranges

/**
 * @author _kisman_
 * @since 11:37 of 01.08.2023
 */
class WrappedClosedRange<T : Comparable<T>>(
    original : ClosedRange<T>
) : ClosedRange<T> {
    var step = Double.NaN

    override val start = original.start
    override val endInclusive = original.endInclusive
}

infix fun <T : Comparable<T>> ClosedRange<T>.step(
    step : T
) = if(this is WrappedClosedRange) {
    this
} else {
    WrappedClosedRange(this)
}.also {
    it.step = (step as Number).toDouble()
}