package lavahack.client.utils.client.interfaces

import lavahack.client.utils.client.enums.HudAnchors
import lavahack.client.utils.mc

/**
 * @author _kisman_
 * @since 11:03 of 24.05.2023
 */
interface IRect {
    var x : Float
    var y : Float
    var w : Float
    var h : Float

    var bound : HudAnchors
        get() = HudAnchors.None
        set(_) { }

//    var anchor : IDraggable?
//    var anchored : IDraggable?

    fun fromString(
        string : String
    ) {
        val split = string.split(":")

        if(split.size == 2) {
            val x = split[0].toFloatOrNull() ?: this.x
            val y = split[1].toFloatOrNull() ?: this.y

            this.x = x
            this.y = y
        }
    }

    fun correct() {
        x = x.coerceIn(0f..(mc.window.scaledWidth - w))
        y = y.coerceIn(0f..(mc.window.scaledHeight - h))
    }

    fun intersects(
        draggable : IRect
    ) = x < draggable.x + draggable.w && x + w > draggable.x && y < draggable.y + draggable.h && y + h > draggable.y
}