package lavahack.client.utils.client.enums

import lavahack.client.utils.client.interfaces.IShader
import lavahack.client.utils.render.shader.*

/**
 * @author _kisman_
 * @since 14:47 of 19.06.2023
 */
enum class CoreShaders(
    val screenShader : IShader,
    val worldShader : IShader
) {
    None(DUMMY_SHADER, DUMMY_SHADER),
    GRADIENT(GRADIENT_SCREEN_CORE_SHADER, GRADIENT_WORLD_CORE_SHADER)

    ;

    override fun toString() = screenShader.displayName
}