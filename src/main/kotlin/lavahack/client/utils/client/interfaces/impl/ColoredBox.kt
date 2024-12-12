package lavahack.client.utils.client.interfaces.impl

import lavahack.client.utils.client.interfaces.IBoxColorer
import net.minecraft.util.math.Box

/**
 * @author _kisman_
 * @since 13:45 of 06.07.2023
 */
class ColoredBox(
    minX : Double,
    minY : Double,
    minZ : Double,
    maxX : Double,
    maxY : Double,
    maxZ : Double,
    val fillColorer : IBoxColorer,
    val outlineColorer : IBoxColorer
) : Box(
    minX,
    minY,
    minZ,
    maxX,
    maxY,
    maxZ
)