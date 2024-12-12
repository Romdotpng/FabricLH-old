package lavahack.client.utils.client.interfaces

import lavahack.client.settings.Setting
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.impl.BoxColorer
import java.awt.Color

/**
 * TODO: rewrite as RectColorer
 *
 * @author _kisman_
 * @since 10:41 of 26.05.2023
 */
interface IBoxColorer {
    val color1 : Setting<Color>
    val color2 : Setting<Color>
    val color3 : Setting<Color>
    val color4 : Setting<Color>
    val color5 : Setting<Color>
    val color6 : Setting<Color>
    val color7 : Setting<Color>
    val color8 : Setting<Color>

    fun clone() : IBoxColorer = BoxColorer().also {
        it.color1.value = this.color1.value
        it.color2.value = this.color2.value
        it.color3.value = this.color3.value
        it.color4.value = this.color4.value
        it.color5.value = this.color5.value
        it.color6.value = this.color6.value
        it.color7.value = this.color7.value
        it.color8.value = this.color8.value
    }

    fun applyAlphaCoeff(
        coeff : Double
    ) : IBoxColorer {
        color1.value = Colour(color1.value.red, color1.value.green, color1.value.blue, (color1.value.alpha * coeff).toInt())
        color2.value = Colour(color2.value.red, color2.value.green, color2.value.blue, (color2.value.alpha * coeff).toInt())
        color3.value = Colour(color3.value.red, color3.value.green, color3.value.blue, (color3.value.alpha * coeff).toInt())
        color4.value = Colour(color4.value.red, color4.value.green, color4.value.blue, (color4.value.alpha * coeff).toInt())
        color5.value = Colour(color5.value.red, color5.value.green, color5.value.blue, (color5.value.alpha * coeff).toInt())
        color6.value = Colour(color6.value.red, color6.value.green, color6.value.blue, (color6.value.alpha * coeff).toInt())
        color7.value = Colour(color7.value.red, color7.value.green, color7.value.blue, (color7.value.alpha * coeff).toInt())
        color8.value = Colour(color8.value.red, color8.value.green, color8.value.blue, (color8.value.alpha * coeff).toInt())

        return this
    }
}