package lavahack.client.utils.client.enums

import lavahack.client.utils.render.world.scale
import net.minecraft.util.math.Box

/**
 * @author _kisman_
 * @since 12:31 of 06.07.2023
 */
enum class ProgressBoxModifiers(
    val modifier : (Box, Double) -> Box
) {
    CentredBox({ box, percent ->
        box.scale(percent)
    }),
    BottomColumn({ box, percent ->
        Box(
            box.minX,
            box.minY,
            box.minZ,
            box.maxX,
            box.maxY - (box.maxY - box.minY) * (1 - percent),
            box.maxZ
        )
    }),
    TopColumn({ box, percent : Double ->
        Box(
            box.minX,
            box.minY + (box.maxY - box.minY) * (1 - percent),
            box.minZ,
            box.maxX,
            box.maxY,
            box.maxZ
        )
    })
}