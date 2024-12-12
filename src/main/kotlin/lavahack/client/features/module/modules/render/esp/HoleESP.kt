@file:Suppress("PropertyName", "PrivatePropertyName")

package lavahack.client.features.module.modules.render.esp

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.HoleProcessor
import lavahack.client.settings.pattern.patterns.BoxRenderingPattern
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.SettingRegistry
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.worldListener
import lavahack.client.utils.world.Hole
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box

/**
 * @author _kisman_
 * @since 14:06 of 28.07.2023
 */
@Module.Info(
    name = "HoleESP",
    display = "Holes",
    description = "Highlights holes around you",
    submodule = true,
    holeprocessor = true
)
class HoleESP : Module() {
    init {
        val safeSingle = register(Settings(Hole.Safety.Safe, Hole.Type.Single))
        val unsafeSingle = register(Settings(Hole.Safety.Unsafe, Hole.Type.Single))
        val safeDouble = register(Settings(Hole.Safety.Safe, Hole.Type.Double))
        val unsafeDouble = register(Settings(Hole.Safety.Unsafe, Hole.Type.Double))
        val safeQuad = register(Settings(Hole.Safety.Safe, Hole.Type.Quad))
        val unsafeQuad = register(Settings(Hole.Safety.Unsafe, Hole.Type.Quad))

        fun settings(
            safety : Hole.Safety,
            type : Hole.Type
        ) = when(type) {
            Hole.Type.Single -> when (safety) {
                Hole.Safety.Safe -> safeSingle
                Hole.Safety.Unsafe -> unsafeSingle
            }

            Hole.Type.Double -> when (safety) {
                Hole.Safety.Safe -> safeDouble
                Hole.Safety.Unsafe -> unsafeDouble
            }

            Hole.Type.Quad -> when (safety) {
                Hole.Safety.Safe -> safeQuad
                Hole.Safety.Unsafe -> unsafeQuad
            }
        }

        worldListener {
            for(hole in HoleProcessor.holes) {
                val settings = settings(hole.safety, hole.type)

                settings.draw(it.matrices, hole)
            }
        }
    }

    class Settings(
        safety : Hole.Safety,
        type : Hole.Type
    ) : ISettingRegistry {
        override val registry = SettingRegistry()

        private val GROUP = register(SettingGroup("$safety $type"))

        private val PATTERN = register(GROUP.add(BoxRenderingPattern()))
        private val HEIGHT = register(GROUP.add(SettingNumber("Height", 1.0, 0.0..1.0)))

        init {
            GROUP.prefix("$safety $type")
        }

        fun draw(
            matrices : MatrixStack,
            hole : Hole
        ) {
            fun modify(
                box : Box
            ) = Box(
                box.minX,
                box.minY,
                box.minZ,
                box.maxX,
                box.minY + (box.maxY - box.minY) * HEIGHT.value,
                box.maxZ
            )

            if(PATTERN.willDraw()) {
                PATTERN.draw(matrices, modify(hole.box))
            }
        }
    }
}