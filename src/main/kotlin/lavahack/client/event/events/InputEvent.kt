package lavahack.client.event.events

import lavahack.client.event.bus.Event
import lavahack.client.utils.client.enums.KeyActions

/**
 * @author _kisman_
 * @since 13:35 of 08.05.2023
 */
open class InputEvent : Event() {
    class Keyboard(val key : Int, val modifiers : Int, val action : KeyActions) : InputEvent()
    class Mouse(val button : Int, val modifiers : Int, val action : KeyActions) : InputEvent()
}