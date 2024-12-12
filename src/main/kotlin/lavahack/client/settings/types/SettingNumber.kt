package lavahack.client.settings.types

import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.ICaster

/**
 * @author _kisman_
 * @since 15:23 of 09.05.2023
 */
@Suppress("UNCHECKED_CAST")
class SettingNumber<N : Comparable<N>>(
    name : String,
    value : N,
    val range : ClosedRange<N>,
    title : String = name,
    onChange : (Setting<N>) -> Unit = {  }
) : Setting<N>(
    name,
    value,
    title,
    onChange
) {
    fun set(
        number : Number
    ) {
        val casted = when(value) {
            is Int -> number.toInt()
            is Double -> number.toDouble()
            is Float -> number.toFloat()
            is Long -> number.toLong()
            is Byte -> number.toByte()
            is Short -> number.toShort()
            else -> throw IllegalArgumentException("Cannot cast ${value.javaClass.name} to int/double/float/long/byte/short")
        } as N

        value = casted
    }

    override fun visible(
        value : () -> Boolean
    ) = super.visible(value) as SettingNumber<N>

    class Caster<N : Comparable<N>> : ICaster<SettingNumber<N>> {
        override fun cast(
            setting : Any
        ) = setting as SettingNumber<N>
    }
}