package lavahack.client.utils.client.enums

import lavahack.client.utils.math.*
import kotlin.math.PI
import kotlin.math.pow

/**
 * @author _kisman_
 * @since 15:40 of 04.07.2023
 */
enum class Easings(
    val function : (Double) -> Double
) {
    Linear({ it }),

    Curve({ curve(it).toDouble() }),

    //SemiCurve({ kotlin.math.sqrt((1.0 - (it - 1.0) * (it - 1.0)).pow(1.6)) }),

    //SmoothedSemiCurve({ kotlin.math.sqrt((1.0 - (it - 1.0) * (it - 1.0)).pow(3.4)) }),

    InSine({ 1.0 - cos(it * PI / 2.0) }),
    OutSine({ sin(it * PI / 2.0).toDouble() }),
    InOutSine({ -(cos(PI * it) - 1.0) / 2.0 }),

    InQuad({ it * it }),
    OutQuad({ 1.0 - (1.0 - it) * (1.0 - it) }),
    InOutQuad({ if(it < 0.5) 2 * it * it else 1.0 - ((-2 * it + 2) * (-2 * it + 2)) / 2 }),

    InCubic({ it * it * it }),
    OutCubic({ 1.0 - (1.0 - it) * (1.0 - it) * (1.0 - it) }),
    InOutCubic({ if(it < 0.5) 4 * it * it * it else 1.0 - ((-2 * it + 2) * (-2 * it + 2) * (-2 * it + 2)) / 2 }),

    InQuart({ it * it * it * it }),
    OutQuart({ 1.0 - (1.0 - it) * (1.0 - it) * (1.0 - it) * (1.0 - it) }),
    InOutQuart({ if(it < 0.5) 8 * it * it * it * it else 1.0 - ((-2 * it + 2) * (-2 * it + 2) * (-2 * it + 2) * (-2 * it + 2)) / 2 }),

    InQuint({ it * it * it * it * it }),
    OutQuint({ 1.0 - (1.0 - it) * (1.0 - it) * (1.0 - it) * (1.0 - it) * (1.0 - it) }),
    InOutQuint({ if(it < 0.5) 16 * it * it * it * it else 1.0 - ((-2 * it + 2) * (-2 * it + 2) * (-2 * it + 2) * (-2 * it + 2) * (-2 * it + 2)) / 2 }),

    InExpo({ if(it == 0.0) 0.0 else 2.0.pow(10 * it - 10) }),
    OutExpo({ if(it == 1.0) 1.0 else 1.0 - 2.0.pow(-10 * it) }),
    InOutExpo({ if(it == 0.0 || it == 1.0) it else if(it < 0.5) 2.0.pow(20 * it - 10) / 2.0 else (2.0 - 2.0.pow(-20 * it + 10)) / 2 }),

    InCircle({ 1.0 - sqrt(1.0 - it * it) }),
    OutCircle({ sqrt(1.0 - (it - 1.0) * (it - 1.0)).toDouble() }),
    InOutCircle({ if(it < 0.5) (1.0 - sqrt(1.0 - (2 * it) * (2 * it))) / 2 else (sqrt(1.0 - (-2 * it + 2) * (-2 * it + 2)) + 1) / 2.0 }),

    InSin({ sin2(it) }),
    OutSin({ 1.0 - sin2(1.0 - it) })

    ;

    fun inc(
        n : Number
    ) = function(n.toDouble()).coerceIn(0.0..1.0)

    fun dec(
        n : Number
    ) = 1.0 - inc(n)

    fun dec(
        n : Double,
        range : ClosedRange<Double>
    ) = lerp(range.start, range.endInclusive, n)
}