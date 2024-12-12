package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.MinecraftEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.math.gaussianValues
import lavahack.client.utils.mc
import lavahack.client.utils.render.shader.GAUSSIAN_BLUR_CORE_SHADER
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import kotlin.math.min

@Suppress("UNCHECKED_CAST")
object GaussianBlurRenderer : SubSystem(
    "Gaussian Blur Renderer",
    true
) {
    var RADIUS : Setting<Float>? = null

    var WEIGHTS : FloatArray? = null
        get() {
            if(field == null) {
                val buffer = BufferUtils.createFloatBuffer(256)

                field = FloatArray(256)

                for(i in 0..min(RADIUS!!.value.toInt(), 256)) {
                    buffer.put(gaussianValues(i, RADIUS!!.value / 2f))
                }

                buffer.rewind()
                buffer.get(field)
            }

            return field
        }

//    val states = mutableListOf<() -> Boolean>()

    override fun preinit() {
        register(GAUSSIAN_BLUR_CORE_SHADER)
    }

    override fun init() {
        //TODO: rewrite it
        RADIUS = GAUSSIAN_BLUR_CORE_SHADER.registry.names.filter { it.key.contains("radius") }.firstNotNullOfOrNull { it.value } as Setting<Float>

        listener<MinecraftEvent.GameRenderer.Render.End> {
            WEIGHTS = null
        }

        /*tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            for(state in states) {
                if(state()) {
                    WEIGHTS = BufferUtils.createFloatBuffer(256)

                    for(i in 0..RADIUS.value.toInt()) {
                        WEIGHTS!!.put(gaussianValues(i, RADIUS.value / 2f))
                    }

                    WEIGHTS!!.rewind()

                    return@tickListener
                }
            }
        }*/
    }
}