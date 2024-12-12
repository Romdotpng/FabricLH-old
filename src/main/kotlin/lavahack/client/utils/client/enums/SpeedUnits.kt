package lavahack.client.utils.client.enums

/**
 * @author _kisman_
 * @since 17:52 of 31.05.2023
 */
enum class SpeedUnits(
    val modify : (Double) -> Double,
    val display : String
) {
    BPS({ it * 20.0 }, "b/s"),
    KMH({ (it / 1000.0) / (0.05 / 3600.0) }, "km/h")
}