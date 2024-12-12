package lavahack.client.features.module.modules.render

import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.features.subsystem.subsystems.PostprocessShaderRenderer
import lavahack.client.mixins.InvokerGameRenderer
import lavahack.client.mixins.InvokerWorldRenderer
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.worldListener
import net.minecraft.entity.player.PlayerEntity

@Module.Info(
    name = "Shaders",
    description = "do i really need to explain that?",
    category = Module.Category.RENDER
)
object ShadersModule : Module() {
    private val HANDS = register(Setting("Hands", false))
    val PLAYERS = register(Setting("Players", false))

    private val ORIGINAL = register(Setting("Original", false))

    init {
        disableCallback {
            mc.gameRenderer.setRenderHand(true)
        }

        worldListener {
//            println("\tshaders listener")

            mc.gameRenderer.setRenderHand(!HANDS.value)

            if(HANDS.value) {
                PostprocessShaderRenderer.render(ORIGINAL.value, -1) {
                    (mc.gameRenderer as InvokerGameRenderer).renderHand0(it.matrices, mc.gameRenderer.camera, mc.tickDelta)
                }
            }
        }
    }
}