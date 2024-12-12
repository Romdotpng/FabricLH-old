package lavahack.client.utils.client.collections

/**
 * @author _kisman_
 * @since 5:54 of 16.05.2023
 */
open class Quad<A, B, C, D>(
    var first : A,
    var second : B,
    var third : C,
    var fourth : D
)

class IntQuad(
    first : Int,
    second : Int,
    third : Int,
    fourth : Int
) : Quad<Int, Int, Int, Int>(
    first,
    second,
    third,
    fourth
)