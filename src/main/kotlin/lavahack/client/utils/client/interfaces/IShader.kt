package lavahack.client.utils.client.interfaces

import lavahack.client.settings.Setting
import net.minecraft.client.util.math.MatrixStack

/**
 * @author _kisman_
 * @since 10:29 of 19.06.2023
 */
interface IShader : ISettingRegistry {
    val displayName : String

    val uniforms : MutableMap<String, Setting<*>>
    val samplers : MutableList<String>

    var created : Boolean

    var hasPrevSampler : Boolean
    var hasMinecraftSampler : Boolean

    fun parse()
    fun create()

    fun bind() { }

    fun unbind() { }

    fun begin() { }
    fun begin(matrices : MatrixStack) { }

    fun end() { }

    fun render() { }
}