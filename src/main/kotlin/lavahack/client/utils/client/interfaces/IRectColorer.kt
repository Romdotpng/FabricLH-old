package lavahack.client.utils.client.interfaces

import lavahack.client.utils.Colour
import lavahack.client.utils.client.enums.ColorerModes
import java.awt.Color

@Suppress("UNCHECKED_CAST")
interface IRectColorer <T : () -> Color> {
    var color1 : T
    var color2 : T
    var color3 : T
    var color4 : T

    fun set(
        color : Color
    ) { }

    fun apply(
        mode : ColorerModes
    ) : IRectColorer<() -> Color> = this as IRectColorer<() -> Color>

    fun clone() : IRectColorer<T> = this

    fun applyAlphaCoeff(
        coeff : Double
    ) : IRectColorer<T> = this
}