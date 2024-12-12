package lavahack.client.utils.client.interfaces

import lavahack.client.features.subsystem.subsystems.InputController
import lavahack.client.utils.buttonName
import lavahack.client.utils.client.enums.BindTypes
import lavahack.client.utils.keyName
import org.lwjgl.glfw.GLFW

interface IBindable {
    var keyboardKey : Int
    var mouseButton : Int
    var type : BindTypes
    var hold : Boolean
    val buttonName : String

    companion object {
        fun getKey(bindable : IBindable) : Int {
            return when(bindable.type) {
                BindTypes.Keyboard -> bindable.keyboardKey
                BindTypes.Mouse -> bindable.mouseButton
            }
        }

        fun getName(bindable : IBindable) : String {
            return when(bindable.type) {
                BindTypes.Keyboard -> if(bindable.keyboardKey == -1) "NONE" else keyName(bindable.keyboardKey)
                BindTypes.Mouse -> buttonName(bindable.mouseButton)
            }
        }

        fun valid(bindable : IBindable) : Boolean {
            return when(bindable.type) {
                BindTypes.Keyboard -> bindable.keyboardKey != -1 && bindable.keyboardKey != GLFW.GLFW_KEY_ESCAPE
                BindTypes.Mouse -> bindable.mouseButton > 1
            }
        }

        fun isPressed(
            bindable : IBindable
        ) = when(bindable.type) {
            BindTypes.Keyboard -> InputController.pressedKey(bindable.keyboardKey)
            BindTypes.Mouse -> InputController.pressedButton(bindable.mouseButton)
        }

        fun bindKey(bindable : IBindable, key : Int) {
            bindable.keyboardKey = key
            bindable.type = BindTypes.Keyboard
        }

        fun bindButton(bindable : IBindable, button : Int) {
            bindable.mouseButton = button
            bindable.type = BindTypes.Mouse
        }
    }
}