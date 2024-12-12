package lavahack.client.utils.client.enums

import lavahack.client.utils.client.interfaces.IShader
import lavahack.client.utils.render.shader.*

/**
 * @author _kisman_
 * @since 11:42 of 04.06.2023
 */
enum class PostprocessShaders(
    val shader : IShader
) {
    DEFAULT(DUMMY_SHADER),
    GRADIENT(GRADIENT_POSTPROCESS_SHADER),
    GLOW_GRADIENT(GLOW_GRADIENT_POSTPROCESS_SHADER)

    ;

    override fun toString() = shader.displayName
}