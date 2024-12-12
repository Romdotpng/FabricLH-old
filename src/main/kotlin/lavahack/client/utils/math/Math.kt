@file:Suppress("FloatingPointLiteralPrecision", "UNCHECKED_CAST")

package lavahack.client.utils.math

import lavahack.client.utils.*
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI

/**
 * @author _kisman_
 * @since 20:01 of 28.05.2023
 */

const val DEG = 57.29577951308232f
const val RAD = 0.017453292f

const val ALMOST_ZERO = 1e-4f

//TODO: convert to constants
val SQRT_OF_2 = sqrt(2)
val SQRT_OF_PI = sqrt(PI)

fun Quaternionf.rotate(
    x : Number,
    y : Number,
    z : Number
) = this.also {
    if(x.toFloat() != 0f) {
        it.rotationX(x.toFloat() * RAD)
    }

    if(y.toFloat() != 0f) {
        it.rotationY(y.toFloat() * RAD)
    }

    if(z.toFloat() != 0f) {
        it.rotationZ(z.toFloat() * RAD)
    }
}

fun roundToPlace(
    number : Number,
    places : Int,
) = BigDecimal(number.toDouble()).setScale(places, RoundingMode.HALF_UP).toDouble()

fun roundToClosest(
    number : Number,
    low : Number,
    high : Number
) = if(high - number >= number - low) {
    low
} else {
    high
}.toDouble()

fun <N : Comparable<N>> N.square() = ((this as Number).toDouble() * (this as Number).toDouble()) as N

fun sin(
    rad : Number
) = MathHelper.sin(rad.toFloat())

fun cos(
    rad : Number
) = MathHelper.cos(rad.toFloat())

//TODO: cache
fun tan(
    rad : Number
) = kotlin.math.tan(rad.toFloat())

fun atan2(
    y : Number,
    x : Number
) = MathHelper.atan2(y.toDouble(), x.toDouble())

fun sqrt(
    n : Number
) = kotlin.math.sqrt(n.toFloat())

fun exp(
    n : Number
) = kotlin.math.exp(n.toFloat())

fun curve(
    a : Number
) = sqrt(1.0 - (a.toDouble() - 1.0) * (a.toDouble() - 1.0))

fun lerp(
    from : Number,
    to : Number,
    delta : Number
) = from.toDouble() + (to.toDouble() - from.toDouble()) * delta.toDouble()

fun lerp(
    color1 : Color,
    color2 : Color,
    delta : Number
) = Color(
    lerp(color1.red, color2.red, delta).toInt(),
    lerp(color1.green, color2.green, delta).toInt(),
    lerp(color1.blue, color2.blue, delta).toInt(),
    lerp(color1.alpha, color2.alpha, delta).toInt()
)

fun delta(
    timestamp : Number,
    time : Number
) = (System.currentTimeMillis() - timestamp.toLong()) / time.toDouble()

//section easing sin function
fun sin2(
    x : Number
) = 1.0 - (0.5 * sin(PI * x.toDouble() + PI / 2.0) + 0.5)

fun hypotenuse(
    x : Number,
    y : Number
) = sqrt(x.toDouble() * x.toDouble() + y.toDouble() * y.toDouble())

//section triangulation

fun intersection(
    first : Pair<Vec3d, Float>,
    second : Pair<Vec3d, Float>
) : Vec3d? {
    val vec1 = first.first
    val vec2 = second.first
    val yaw1 = first.second
    val yaw2 = second.second

    return if(yaw1 == yaw2) {
        null
    } else {
        val x1 = vec1.x
        val y1 = vec1.y
        val z1 = vec1.z

        val x2 = vec2.x - x1
        val y2 = vec2.y
        val z2 = vec2.z - z1

        val k1 = tan(yaw1 * RAD)
        val k2 = tan(yaw2 * RAD)

        val b2 = x2 - k2 * z2

        var z3 = b2 / (k1 - k2)
        var x3 = k1 * z3

        x3 += x1
        z3 += z1

        val y3 = (y1 + y2) / 2.0

        return Vec3d(x3, y3, z3)
    }
}

//section gaussian blur

//TODO: optimize exp
fun gaussianValues(
    x : Number,
    sigma : Number
) = (exp(-(x * x) / 2.0 * sigma * sigma) / (SQRT_OF_2 * SQRT_OF_PI * sigma)).toFloat()