package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.box
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.worldListener
import lavahack.client.utils.reachableSides
import lavahack.client.utils.render.world.correct
import lavahack.client.utils.render.world.fill
import net.minecraft.util.hit.BlockHitResult
import java.awt.Color

@Module.Info(
    name = "StrictDirectionTest",
    description = "Test of strict direction feature",
    category = Module.Category.DEBUG
)
class StrictDirectionTest : Module() {
    init {
        val shaders = register(Setting("Shaders", false))

        worldListener {
            val result = mc.crosshairTarget

            if(result is BlockHitResult) {
                val pos = result.blockPos!!
                val sides = pos.reachableSides()

                for(side in sides) {
                    val offset = pos.offset(side)

                    if(shaders.value) {
                        CoreShaders.GRADIENT.worldShader.begin()
                    }

                    fill(
                        it.matrices,
                        offset.box().correct(),
                        Color(255, 255, 255, 120),
                        Color(255, 255, 255, 120),
                        Color(255, 255, 255, 120),
                        Color(255, 255, 255, 120),
                        Color(255, 255, 255, 120),
                        Color(255, 255, 255, 120),
                        Color(255, 255, 255, 120),
                        Color(255, 255, 255, 120)
                    )

                    if(shaders.value) {
                        CoreShaders.GRADIENT.worldShader.end()
                    }
                }
            }
        }
    }
}