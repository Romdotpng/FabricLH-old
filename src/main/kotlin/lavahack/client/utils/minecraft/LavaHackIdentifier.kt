package lavahack.client.utils.minecraft

import lavahack.client.LavaHack
import net.minecraft.util.Identifier

/**
 * @author _kisman_
 * @since 16:24 of 03.06.2023
 */
class LavaHackIdentifier(
    path : String
) : Identifier(
    LavaHack.MODID,
    path
)