package lavahack.client.settings.types.combo

import lavahack.client.settings.Setting
import lavahack.client.utils.client.enums.BindTypes
import lavahack.client.utils.client.interfaces.impl.Binder

/**
 * @author _kisman_
 * @since 10:06 of 19.05.2023
 */
@Suppress("LocalVariableName")
class Element<T>(
    _element : T,
    val list : List<T>,
    val onChange : (Element<T>, Setting<Element<T>>?) -> Unit = { _, _ -> }
) {
    var current = _element
        set(value) {
            prev = field
            field = value

            if(prev != value) {
                onChange(this, setting)
            }
        }

    var prev : T? = null

    val binders = list.stream().map { Binder(it.toString(), BindTypes.Keyboard, -1, -1, false) }

    var setting : Setting<Element<T>>? = null

    override fun toString() = current.toString()

    fun fromString(
        name : String
    ) = this.also {
        val new = list.stream().filter { it!!.toString() == name }.findFirst().orElse(null)

        if(new != null) {
            current = new
        }
    }
}