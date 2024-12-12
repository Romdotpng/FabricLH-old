package lavahack.client.utils.client.interfaces.impl

import lavahack.client.features.config.StoredData
import lavahack.client.utils.client.interfaces.IRect
import lavahack.client.utils.client.interfaces.IStorable
import lavahack.client.utils.client.interfaces.IVisible

/**
 * @author _kisman_
 * @since 13:03 of 24.05.2023
 */
class Hitbox(
    private val _visible : () -> Boolean
) : IRect, IVisible, IStorable {
    override var x = 1f
    override var y = 1f
    override var w = 1f
    override var h = 1f

//    override var anchor : IDraggable? = null
//    override var anchored : IDraggable? = null

    override fun visible() = _visible()
    override fun toString() = "$x:$y"

    override fun save() = StoredData(
        "NULL",
        "x", x,
        "y", y
    )

    override fun load(
        data : StoredData
    ) {
        x = data.float("x") ?: x
        y = data.float("y") ?: y
    }

    companion object {
        fun fromString(
            string : String,
            default : Hitbox
        ) = Hitbox(default._visible).also {
            val split = string.split(":")

            if(split.size == 2) {
                val x = split[0].toFloatOrNull() ?: 1f
                val y = split[1].toFloatOrNull() ?: 1f

                it.x = x
                it.y = y
            }
        }
    }
}

class Rect(
    override var x : Float,
    override var y : Float,
    override var w : Float,
    override var h : Float
) : IRect {
    constructor(
        x : Number,
        y : Number,
        w : Number,
        h : Number
    ) : this(
        x.toFloat(),
        y.toFloat(),
        w.toFloat(),
        h.toFloat()
    )
}

val DUMMY_RECT = Rect(0, 0, 0, 0)