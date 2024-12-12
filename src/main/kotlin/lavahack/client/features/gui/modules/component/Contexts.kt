@file:Suppress("UNUSED_PARAMETER")

package lavahack.client.features.gui.modules.component

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.utils.client.interfaces.IRect
import lavahack.client.utils.client.interfaces.IVisible

interface IComponentContext {
    var x : Double
    var y : Double
    var offset : Double
    var count : Int
    var layer : Int

    var visible : IVisible

    fun clone() = ComponentContext(
        x,
        y,
        offset,
        count,
        layer,
        visible
    )

    fun visible(
        visible : IVisible
    ) = this.also {
        this.visible = visible
    }

    fun layer(
        layer : Int
    ) = this.also {
        this.layer = layer
    }
}

class EmptyContext : IComponentContext {
    override var x = 0.0
    override var y = 0.0
    override var offset = 0.0
    override var count = 0
    override var layer = 0
    override var visible : IVisible = object : IVisible { override fun visible() = true }
}

open class ComponentContext(
    override var x : Double,
    override var y : Double,
    override var offset : Double,
    override var count : Int,
    override var layer : Int,
    override var visible : IVisible = object : IVisible { override fun visible() = true }
) : IComponentContext

class XYContext(
    x : Double,
    y : Double
) : ComponentContext(
    x,
    y,
    0.0,
    0,
    0
)

class DescriptionContext(
    private val parent : IComponentContext
) : IComponentContext {
    override var x
        get() = ModuleGui.mouseX.toDouble()
        set(value) {}

    override var y
        get() = ModuleGui.mouseY.toDouble()
        set(value ) {}

    override var offset
        get() = parent.offset
        set(value) {}

    override var count
        get() = parent.count
        set(value) {}

    override var layer = 0
    override var visible = parent.visible
}

class DraggableContext(
    private val draggable : IRect
) : IComponentContext {
    override var x : Double
        get() = draggable.x.toDouble()
        set(value) {}

    override var y : Double
        get() = draggable.y.toDouble()
        set(value) {}

    override var offset : Double
        get() = 0.0
        set(value) {}

    override var count = 0
    override var layer = 0
    override var visible = object : IVisible { override fun visible() = true }
}