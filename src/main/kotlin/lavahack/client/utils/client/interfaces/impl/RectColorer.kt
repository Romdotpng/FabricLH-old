package lavahack.client.utils.client.interfaces.impl

import lavahack.client.settings.Setting
import lavahack.client.utils.Colour
import lavahack.client.utils.client.enums.ColorerModes
import lavahack.client.utils.client.interfaces.IRectColorer
import lavahack.client.utils.client.interfaces.ISettingRegistry
import java.awt.Color

class RectColorer : IRectColorer<Setting<Color>>, ISettingRegistry {
    override val registry = SettingRegistry()

    override var color1 = register(Setting("Color 1", Colour(255, 0, 0, 255) as Color, "1st"))
    override var color2 = register(Setting("Color 2", Colour(255, 0, 0, 255) as Color, "2nd"))
    override var color3 = register(Setting("Color 3", Colour(255, 0, 0, 255) as Color, "3rd"))
    override var color4 = register(Setting("Color 4", Colour(255, 0, 0, 255) as Color, "4th"))

    override fun set(
        color : Color
    ) {
        color1.value = color
        color2.value = color
        color3.value = color
        color4.value = color
    }

    override fun apply(
        mode : ColorerModes
    ) = Empty().also {
        when(mode) {
            ColorerModes.Single -> {
                it.color1 = color1
                it.color2 = color1
                it.color3 = color1
                it.color4 = color1
            }

            ColorerModes.Double -> {
                it.color1 = color1
                it.color2 = color1
                it.color3 = color2
                it.color4 = color2
            }

            ColorerModes.Chroma -> {
                it.color1 = color1
                it.color2 = color2
                it.color3 = color3
                it.color4 = color4
            }
        }
    }

    override fun clone() = RectColorer().also {
        it.color1.value = this.color1()
        it.color2.value = this.color2()
        it.color3.value = this.color3()
        it.color4.value = this.color4()
    }

    override fun applyAlphaCoeff(
        coeff : Double
    ) : IRectColorer<Setting<Color>> {
        color1.value = Colour(color1().red, color1().green, color1().blue, (color1().alpha * coeff).toInt())
        color2.value = Colour(color2().red, color2().green, color2().blue, (color2().alpha * coeff).toInt())
        color3.value = Colour(color3().red, color3().green, color3().blue, (color3().alpha * coeff).toInt())
        color4.value = Colour(color4().red, color4().green, color4().blue, (color4().alpha * coeff).toInt())

        return this
    }

    class Empty : IRectColorer<() -> Color> {
        override var color1 : () -> Color = { Colour(-1) }
        override var color2 : () -> Color = { Colour(-1) }
        override var color3 : () -> Color = { Colour(-1) }
        override var color4 : () -> Color = { Colour(-1) }
    }
}