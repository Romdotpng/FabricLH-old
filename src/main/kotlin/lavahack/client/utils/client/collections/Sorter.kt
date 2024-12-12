package lavahack.client.utils.client.collections

import lavahack.client.settings.types.combo.Element
import lavahack.client.utils.render.screen.stringWidth

/**
 * @author _kisman_
 * @since 15:46 of 04.07.2023
 */
class Sorter<T>(
    private val getter : (T) -> String
) {
    val length = Entry("Length", Comparator.comparingInt<T> { stringWidth(getter(it)) }.reversed())
    val alphabet  = Entry("Alphabet", Comparator.comparing<T, String> { getter(it) })

    fun asElement() = Element(
        length,
        listOf(length, alphabet)
    )

    class Entry<T>(
        private val name : String,
        val comparator : Comparator<T>
    ) {
        override fun toString() = name
    }
}