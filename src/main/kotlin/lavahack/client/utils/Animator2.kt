package lavahack.client.utils

import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.interfaces.IAnimatorContext

class Animator2(
    private val easing : () -> Easings,
    private val length : () -> Long,
    private val start : Double,
    private val end : Double
) {
    constructor(
        animator : IAnimatorContext,
        start : Double,
        end : Double
    ) : this(
        { animator.easing },
        { animator.length },
        start,
        end
    )

    private var timestamp = System.currentTimeMillis()
    private var current = start
    private var last = start
    private var reversed = false

    fun update() {
        val length = length()

        current = if(!reversed) {
            if(System.currentTimeMillis() - timestamp >= length) {
                end
            } else {
                last + ((System.currentTimeMillis() - timestamp) / length.toDouble()) * (end - start)
            }
        } else {
            if(System.currentTimeMillis() - timestamp >= length) {
                start
            } else {
                last - ((System.currentTimeMillis() - timestamp) / length.toDouble()) * (end - start)
            }
        }
    }

    fun reset() {
        current = start
        last = start
        timestamp = System.currentTimeMillis()
    }

    fun get(
        applyEasing : Boolean = true
    ) = if(applyEasing) {
        easing().inc(current)
    } else {
        current
    }

    fun animating() = current != start && current != end

    fun reverse(
        state : Boolean
    ) {
        if(reversed != !state) {
            reversed = !state
            last = current
            timestamp = System.currentTimeMillis()
        }
    }

    fun set(
        value : Double
    ) {
        current = value
        last = value
    }
}