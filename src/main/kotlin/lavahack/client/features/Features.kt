package lavahack.client.features

import lavahack.client.event.bus.listener
import lavahack.client.event.events.InputEvent
import lavahack.client.utils.client.enums.BindTypes
import lavahack.client.utils.client.enums.KeyActions
import lavahack.client.utils.client.interfaces.*
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.mc

/**
 * @author _kisman_
 * @since 13:23 of 08.05.2023
 */

interface IFeature : IBindable

abstract class DisplayableFeature(
    buttonName : String = "",
    type : BindTypes = BindTypes.Keyboard,
    keyboardKey : Int = -1,
    mouseButton : Int = -1,
    hold : Boolean = false
) : IFeature, ISettingRegistry, IStorable, IVisible, IStateAnimator, Binder(
    buttonName,
    type,
    keyboardKey,
    mouseButton,
    hold
) {
    override val registry = SettingRegistry()
    override val animator = StateAnimator()

    init {
        listener<InputEvent.Keyboard> {
            if(mc.player == null || mc.world == null || it.action == KeyActions.Repeat) {
                return@listener
            }

            if(super.type == BindTypes.Keyboard && super.keyboardKey == it.key) {
                if(it.action == KeyActions.Press || (super.hold && it.action == KeyActions.Release)) {
                    onInputEvent()
                }
            }
        }

        listener<InputEvent.Mouse> {
            if(mc.player == null || mc.world == null || it.action == KeyActions.Repeat) {
                return@listener
            }

            if(super.type == BindTypes.Mouse && super.mouseButton == it.button) {
                if(it.action == KeyActions.Press || (super.hold && it.action == KeyActions.Release)) {
                    onInputEvent()
                }
            }
        }
    }

    abstract fun onInputEvent()
}