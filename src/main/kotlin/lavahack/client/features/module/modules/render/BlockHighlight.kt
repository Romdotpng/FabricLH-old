package lavahack.client.features.module.modules.render

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.pattern.patterns.SlideRenderingPattern
import lavahack.client.utils.block
import lavahack.client.utils.box
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.worldListener
import lavahack.client.utils.render.world.SlideRenderer
import net.minecraft.block.AirBlock
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 11:37 of 26.05.2023
 */
@Module.Info(
    name = "BlockHighlight",
    description = "Highlights object you are looking at",
    category = Module.Category.RENDER
)
class BlockHighlight : Module() {
    init {
        val pattern = register(SlideRenderingPattern())
        val renderer = SlideRenderer()

        enableCallback {
            renderer.reset()
        }

        worldListener {
            val result = mc.crosshairTarget
            var toRender : BlockPos? = null

            if(result is BlockHitResult) {
                val pos = result.blockPos
                val block = pos.block()

                if(block !is AirBlock) {
                    toRender = pos
                }
            }

            renderer.handleRender(it.matrices, toRender?.box(), pattern)
        }
    }
}