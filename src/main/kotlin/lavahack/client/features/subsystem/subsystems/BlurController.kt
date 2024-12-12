package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.render.shader.GAUSSIAN_BLUR_CORE_SHADER
import lavahack.client.utils.render.shader.PostProcessShader
import net.minecraft.client.util.math.MatrixStack

//TODO: move to render.shader.ShaderRendering
object BlurController : SubSystem(
    "Blur Controller",
    hasPreinitializer = true
) {
    val SHADER = PostProcessShader("Blur", "blur_postprocess")

    override fun preinit() {
        register(SHADER)
    }
}

fun blurred(
    matrices : MatrixStack,
    render : Boolean = true,
    block : () -> Unit
) {
    GAUSSIAN_BLUR_CORE_SHADER.begin()

    block()

    GAUSSIAN_BLUR_CORE_SHADER.end()

    if(render) {
        block()
    }

    /*BlurController.SHADER.begin(matrices)

    block()

    BlurController.SHADER.end()

    if(render) {
        render()
    }*/
}

fun render() {
    BlurController.SHADER.render()
}