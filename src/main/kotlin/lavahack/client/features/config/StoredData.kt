package lavahack.client.features.config

import lavahack.client.settings.types.combo.Element
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.impl.Hitbox
import java.awt.Color

/**
 * @author _kisman_
 * @since 13:36 of 21.05.2023
 */
@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
class StoredData(
    var prefix : String,
    vararg pairs : Any
) {
    var entries = mutableMapOf<String, String>()

    init {
        var key : String? = null

        for(element in pairs) {
            if(key == null) {
                key = element.toString()
            } else {
                entries[key] = element.toString()
                key = null
            }
        }
    }

    operator fun set(
        key : String,
        value : Any
    ) {
        entries[key] = value.toString()
    }

    fun boolean(
        key : String
    ) = entries[key]?.toBooleanStrictOrNull()

    fun int(
        key : String
    ) = entries[key]?.toIntOrNull()

    fun double(
        key : String
    ) = entries[key]?.toDoubleOrNull()

    fun float(
        key : String
    ) = entries[key]?.toFloatOrNull()

    fun long(
        key : String
    ) = entries[key]?.toLongOrNull()

    fun string(
        key : String
    ) = entries[key]

    fun <T> type(
        key : String,
        type : T
    ) = when(type) {
        is Int -> int(key)
        is Double -> double(key)
        is Float -> float(key)
        is Long -> long(key)
        is Boolean -> boolean(key)
        is String -> string(key)
        is Colour -> if(int(key) != null) Colour(int(key)!!) else type
        is Color -> if(int(key) != null) Color(int(key)!!) else type
        is Hitbox -> if(string(key) != null) Hitbox.fromString(string(key)!!, type) else type
        is Element<*> -> if(string(key) != null) type.fromString(string(key)!!) else type
        else -> null
    } as T?
}