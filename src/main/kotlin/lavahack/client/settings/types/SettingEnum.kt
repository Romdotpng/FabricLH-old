package lavahack.client.settings.types

import lavahack.client.settings.Setting
import lavahack.client.settings.types.combo.Element
import lavahack.client.utils.client.interfaces.ICaster
import java.util.*

/**
 * @author _kisman_
 * @since 11:22 of 19.05.2023
 */
@Suppress("UNCHECKED_CAST")
open class SettingEnum<E : Enum<*>>(
    name : String,
    value : E,
    title : String = name,
    onChange : (Setting<Element<E>>) -> Unit = {  }
) : Setting<Element<E>>(
    name,
    Element(value, Arrays.stream(value.javaClass.enumConstants).toList()),
    title,
    onChange
) {
    val valEnum : E
        get() = value.current

    override fun visible(
        value : () -> Boolean
    ) = super.visible(value) as SettingEnum<E>

    override fun link(
        state : () -> Boolean,
        value : () -> Element<E>,
        visible : (() -> Boolean)?
    ) = super.link(state, value, visible) as SettingEnum<E>

    override fun link(
        pair : Pair<() -> Boolean, () -> Element<E>>
    ) = super.link(pair) as SettingEnum<E>

    class Caster<E : Enum<*>> : ICaster<SettingEnum<E>> {
        override fun cast(
            setting : Any
        ) = setting as SettingEnum<E>
    }
}