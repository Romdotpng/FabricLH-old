package lavahack.client.utils.client.enums

/**
 * @author _kisman_
 * @since 10:45 of 24.06.2023
 */
enum class Protocols(
    private val display : String
) {
    Old("1.12.2"),
    New("1.13+")

    ;

    override fun toString() = display
}