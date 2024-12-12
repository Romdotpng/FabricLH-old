package lavahack.client.features.module.modules.debug

import lavahack.client.features.module.Module
import lavahack.client.utils.client.interfaces.impl.BoxColorer
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.client.interfaces.impl.worldListener
import lavahack.client.utils.raycast
import lavahack.client.utils.render.world.box
import lavahack.client.utils.render.world.correct
import lavahack.client.utils.render.world.full
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@Module.Info(
    name = "RaycastTest",
    description = "Test of mc.world.raycastBlock()",
    category = Module.Category.DEBUG
)
class RaycastTest : Module() {
    init {
        val colorer = register(BoxColorer())

        var result : BlockHitResult? = null

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            result = raycast(mc.player!!.eyePos, mc.player!!.blockPos.add(5, -2, 5).toCenterPos())
        }

        worldListener {
            if(result != null) {
                full(it.matrices, Box.from(result!!.pos).correct(), colorer, null, 0)
                full(it.matrices, Box.from(mc.player!!.blockPos.add(5, -2, 5).toCenterPos()).correct(), colorer, null, 0)
            }
        }
    }
}