package lavahack.client.features.module.modules.misc

import lavahack.client.event.events.InputEvent
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BoxRenderingPattern
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.chat.ChatUtility
import lavahack.client.utils.client.enums.KeyActions
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.math.intersection
import lavahack.client.utils.wrap
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW

@Module.Info(
    name = "Triangulator",
    description = "Triangulates intersection of 2 directions",
    category = Module.Category.MISC
)
class Triangulator : Module() {
    init {
        val saveDirectionKey = register(Setting("Save Direction", Binder("Save Direction", keyboardKey = GLFW.GLFW_KEY_G)))
        val resetDirectionsKey = register(Setting("Reset Direction", Binder("Reset Direction", keyboardKey = GLFW.GLFW_KEY_H)))
        val debug = register(Setting("Debug", true))
        val renderGroup = register(SettingGroup("Render"))
        val renderer = register(renderGroup.add(BoxRenderingPattern(tracer = true)))

        renderGroup.prefix("Box")

        var first : Pair<Vec3d, Float>? = null
        var second : Pair<Vec3d, Float>? = null
        var intersection : Vec3d? = null

        worldListener {
            if(intersection != null) {
                val pos = BlockPos.ofFloored(intersection!!.x, intersection!!.y, intersection!!.z)

                renderer.draw(it.matrices, pos)
            }
        }

        //TODO: event for key and mouse things or make Binder as KeyBinding class
        listener<InputEvent.Keyboard> {
            if(mc.player == null || mc.world == null || mc.currentScreen != null) {
                return@listener
            }

            if(it.action == KeyActions.Press) {
                if(it.key == saveDirectionKey.value.keyboardKey) {
                    intersection = null

                    if(first == null) {
                        first = mc.player!!.pos to mc.player!!.yaw.wrap()

                        if(debug.value) {
                            ChatUtility.INFO.print("Saved first direction")
                        }
                    } else if(second == null) {
                        second = mc.player!!.pos to mc.player!!.yaw.wrap()

                        if(debug.value) {
                            ChatUtility.INFO.print("Saved second direction")
                        }
                    } else {
                        intersection = intersection(first!!, second!!)
                        first = null
                        second = null

                        if(debug.value) {
                            if(intersection != null) {
                                ChatUtility.INFO.print("Intersection is ${intersection!!.x.toInt()}, ${intersection!!.z.toInt()}")
                            } else {
                                ChatUtility.INFO.print("Directions are parallel")
                            }
                        }
                    }
                }

                if(it.key == resetDirectionsKey.value.keyboardKey) {
                    first = null
                    second = null
                    intersection = null

                    if(debug.value) {
                        ChatUtility.INFO.print("Reset directions")
                    }
                }
            }
        }
    }
}