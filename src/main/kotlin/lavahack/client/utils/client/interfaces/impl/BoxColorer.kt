package lavahack.client.utils.client.interfaces.impl

import lavahack.client.settings.Setting
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.IBoxColorer
import lavahack.client.utils.client.interfaces.ISettingRegistry
import java.awt.Color

/**
 * TODO: rewrite as RectColorer
 *
 * @author _kisman_
 * @since 10:42 of 26.05.2023
 */
class BoxColorer : IBoxColorer, ISettingRegistry {
    override val registry = SettingRegistry()

    override val color1 = register(Setting("Color 1", Colour(255, 0, 0, 255) as Color, "1st"))
    override val color2 = register(Setting("Color 2", Colour(255, 0, 0, 255) as Color, "2nd"))
    override val color3 = register(Setting("Color 3", Colour(255, 0, 0, 255) as Color, "3rd"))
    override val color4 = register(Setting("Color 4", Colour(255, 0, 0, 255) as Color, "4th"))
    override val color5 = register(Setting("Color 5", Colour(255, 0, 0, 255) as Color, "5th"))
    override val color6 = register(Setting("Color 6", Colour(255, 0, 0, 255) as Color, "6th"))
    override val color7 = register(Setting("Color 7", Colour(255, 0, 0, 255) as Color, "7th"))
    override val color8 = register(Setting("Color 8", Colour(255, 0, 0, 255) as Color, "8th"))
}