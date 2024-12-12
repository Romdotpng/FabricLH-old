package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.features.subsystem.subsystems.blurred
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.screenListener
import lavahack.client.utils.render.screen.rectWH

/**
 * @author _kisman_
 * @since 23:23 of 03.07.2023
 */
@Module.Info(
    name = "ShaderTest",
    description = "Test of screen shaders",
    category = Module.Category.DEBUG
)
class ShaderTest : Module() {
    init {
        val color = register(Setting("Color", Colour(-1)))
//        val alpha = register(SettingNumber("Alpha", 255, 0..255))
        val x = register(SettingNumber("X", 100, 0..500))
        val y = register(SettingNumber("Y", 100, 0..500))
        val original = register(Setting("Original", false))
//        val blur = register(PostProcessShader("Blur", "blur_postprocess"))

//        register(Shaders2D.CIRCLE_GRADIENT.shader)
//        register(Shaders3D.GRADIENT.shader)

        enableCallback {

        }

        screenListener {
            blurred(it.context.matrices, original.value) {
                rectWH(it.context, x.value, y.value, 100, 100, color.value)
            }

//            Shaders2D.CIRCLE_GRADIENT.shader.start(it.context.matrices)

            //TODO: i need to try to set alpha to 0 or PostEffectProcessor
            /*shader()?.bind()

            setShader(DUMMY_SHADER_PROGRAM)

            rect(
                it.context,
                100,
                100,
                200,
                200,
                Color(255, 255, 255, alpha.value)
            )

            rectWH(
                it.context,
                x.value,
                y.value,
                50,
                100,
                Color(255, 255, 255, alpha.value)
            )

            resetShader()

            shader()?.unbind()*/

//            Shaders2D.CIRCLE_GRADIENT.shader.end(it.context.matrices)
        }
    }
}