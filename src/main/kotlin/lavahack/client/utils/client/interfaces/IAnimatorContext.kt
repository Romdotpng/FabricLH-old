package lavahack.client.utils.client.interfaces

import lavahack.client.utils.client.enums.Easings

/**
 * @author _kisman_
 * @since 16:02 of 04.07.2023
 */
interface IAnimatorContext {
    val easing : Easings
    val length : Long
}