package lavahack.client.utils

import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.interfaces.IAnimatorContext

/**
 * @author _kisman_
 * @since 15:33 of 04.07.2023
 */
class Animator(
    private val easing : () -> Easings,
    private val length : () -> Long,
    reverse : Boolean
) {
    constructor(
        animator : IAnimatorContext,
        reverse : Boolean
    ) : this(
        { animator.easing },
        { animator.length },
        reverse
    )

    private var timestamp = System.currentTimeMillis()

    private val start = if(reverse) 1.0 else 0.0
    private val end = if(reverse) 0.0 else 1.0

    private var current = start

    fun update() {
        val length = length()

        current = if(System.currentTimeMillis() - timestamp >= length) {
            end
        } else {
            start + ((System.currentTimeMillis() - timestamp) / length.toDouble()) * (end - start)
        }
    }

    fun reset() {
        current = start
        timestamp = System.currentTimeMillis()
    }

    fun get(
        applyEasing : Boolean = true
    ) = if(applyEasing) {
        easing().inc(current)
    } else {
        current
    }

    fun animating() = current != end
}