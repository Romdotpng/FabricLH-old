package lavahack.client.features.module.modules.render.esp

import lavahack.client.event.events.PlayerEvent
import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Colour
import lavahack.client.utils.beginWorld
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.client.interfaces.impl.worldListener
import lavahack.client.utils.endWorld
import lavahack.client.utils.render.world.gradientHorizontalCircle
import lavahack.client.utils.render.world.horizontalCircle
import net.minecraft.util.math.Vec3d

@Module.Info(
    name = "JumpCircle",
    display = "Jumps",
    submodule = true
)
class JumpCircle : Module() {
    init {
        val mode = register(SettingEnum("Mode", Mode.Jump))
        val onlySelf = register(Setting("Only Self", true))
        val insideColor = register(Setting("Inside Color", Colour(-1)))
        val outsideColor = register(Setting("Outside Color", Colour(-1)))
        val lineColor = register(Setting("Line Color", Colour(-1)))
        val line = register(Setting("Line", true))
        val insideGradient = register(Setting("Inside Gradient", false))
        val outsideGradient = register(Setting("Outside Gradient", false))
        val gradientOffset = register(SettingNumber("Gradient Offset", 0.1, 0.01..1.0))
        val easing = register(SettingEnum("Easing", Easings.Linear))
        val length = register(SettingNumber("Length", 1000L, 100L..4000L))
        val maxRadius = register(SettingNumber("Max Radius", 2.0, 0.1..3.0))
        val width = register(SettingNumber("Width", 1f, 0.1f..5f))
        val fade = register(Setting("Fade", true))
        val shader = register(SettingEnum("Shader", CoreShaders.None))

        val jumps = mutableMapOf<Long, Vec3d>()

        var lastOnGround = false

        fun reset() {
            jumps.clear()
            lastOnGround = false
        }

        enableCallback {
            reset()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                reset()
                return@tickListener
            }

            if (mode.valEnum != Mode.Jump && mc.player!!.isOnGround && !lastOnGround) {
                val pos = mc.player!!.pos
                val timestamp = System.currentTimeMillis()

                jumps[timestamp] = pos
            }

            lastOnGround = mc.player!!.isOnGround
        }

        listener<PlayerEvent.Jump> {
            if((!onlySelf.value || it.self) && mode.valEnum != Mode.Impact) {
                val pos = mc.player!!.pos
                val timestamp = System.currentTimeMillis()

                jumps[timestamp] = pos
            }
        }

        worldListener {
            for((index, jump) in jumps.toMutableMap().entries.withIndex()) {
                val pos = jump.value
                val timestamp = jump.key
                val delta = System.currentTimeMillis() - timestamp

                if(delta > length.value) { 
                    jumps.remove(timestamp)

                    continue
                }

                val percent = easing.valEnum.inc(delta.toDouble() / length.value.toDouble())
                val radius1 = maxRadius.value * percent
                val alpha = if(fade.value) ((1.0 - percent) * 255.0).toInt() else 255
                //TODO: make it works with gradients
                val color3 = insideColor.value.mix(outsideColor.value, 0.5)

                shader.beginWorld()

                if(insideGradient.value) {
                    val radius2 = radius1 - gradientOffset.value

                    gradientHorizontalCircle(
                        it.matrices,
                        pos,
                        insideColor.value.clone(true).alpha(0),
                        color3.alpha(alpha),
                        radius2,
                        radius1,
                        counter = index
                    )
                }

                if(outsideGradient.value) {
                    val radius2 = radius1 + gradientOffset.value

                    gradientHorizontalCircle(
                        it.matrices,
                        pos,
                        insideColor.value.clone(true).alpha(0),
                        color3.alpha(alpha),
                        radius2,
                        radius1,
                        counter = index
                    )
                }

                if(line.value) {
                    horizontalCircle(
                        it.matrices,
                        pos,
                        lineColor.value.clone(true).alpha(alpha),
                        width.value,
                        radius1
                    )
                }

                shader.endWorld()
            }
        }
    }

    private enum class Mode {
        Jump,
        Impact,
        Both
    }
}