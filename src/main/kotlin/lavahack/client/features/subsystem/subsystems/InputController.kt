package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.mc
import org.lwjgl.glfw.GLFW

/**
 * @author _kisman_
 * @since 11:32 of 08.05.2023
 */
object InputController : SubSystem(
    "Input Controller"
) {
    private val keys = BooleanArray(512)
    private val buttons = BooleanArray(16)

    fun can() = mc.currentScreen == null

    fun keyState(
        key : Int,
        state : Boolean
    ) {
        if(key >= 0 && key < keys.size) {
            keys[key] = state
        }
    }

    fun buttonState(
        button : Int,
        state : Boolean
    ) {
        if(button >= 0 && button < buttons.size) {
            buttons[button] = state
        }
    }

    fun pressedKey(
        key : Int,
        ignoreScreenCheck : Boolean = false
    ) = if(key == GLFW.GLFW_KEY_UNKNOWN || (!ignoreScreenCheck && !can())) {
        false
    } else {
        key < keys.size && keys[key]
    }

    fun pressedButton(
        button : Int,
        ignoreScreenCheck : Boolean = false
    ) = if(button == -1 || (!ignoreScreenCheck && !can())) {
        false
    } else {
        button < buttons.size && buttons[button]
    }
}