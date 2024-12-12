package lavahack.client.features.module.modules.debug

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Animator2
import lavahack.client.utils.Colour
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.screenListener
import lavahack.client.utils.distanceSq
import lavahack.client.utils.math.sqrt
import lavahack.client.utils.render.screen.circleRectWH
import lavahack.client.utils.render.screen.rectWH
import org.joml.Vector2d
import java.awt.Color
import kotlin.math.max

@Module.Info(
    name = "RenderTest",
    category = Module.Category.DEBUG
)
class RenderTest : Module() {
    init {
        val reverse = register(Setting("Reverse", false))
        /*val coeff = register(SettingNumber("Coeff", 0.0, 0.0..1.0))
        val centerX = register(SettingNumber("Center X", 50, 0..100))
        val centerY = register(SettingNumber("Center Y", 10, 0..20))
        val color = register(Setting("Color", Colour(255, 0, 0, 255)))*/

        var prev = false

        val animator = Animator2({ Easings.Linear }, { 1000L }, 0.0, 1.0)

        screenListener {
            /*val centerVec = Vector2d(100.0 + centerX.value.toDouble(), 100.0 + centerY.value.toDouble())
            val vec1 = Vector2d(100.0, 100.0)
            val vec2 = Vector2d(100.0, 120.0)
            val vec3 = Vector2d(200.0, 100.0)
            val vec4 = Vector2d(200.0, 120.0)

            val distance1Sq = centerVec distanceSq vec1
            val distance2Sq = centerVec distanceSq vec2
            val distance3Sq = centerVec distanceSq vec3
            val distance4Sq = centerVec distanceSq vec4

            val maxDistanceSq = max(max(max(distance1Sq, distance2Sq), distance3Sq), distance4Sq)
            val maxDistance = sqrt(maxDistanceSq)

            val radius = maxDistance * coeff.value*/

            animator.reverse(reverse.value)
            animator.update()

            it.context.enableScissor(100, 100, 100 + (100.0 * animator.get()).toInt(), 200)

            rectWH(
                it.context,
                100,
                100,
                100,
                100,
                Color(-1)
            )

            it.context.disableScissor()

            /*circleRectWH(
                it.context,
                100,
                100,
                100,
                100,
                150,
                150,
                70 * animator.get(),
                0,
                Color(-1)
            )*/
        }
    }
}