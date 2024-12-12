@file:Suppress("UNUSED_PARAMETER", "LocalVariableName")

package lavahack.client.features.gui.modules.component

import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.utils.Animator2
import lavahack.client.utils.animate
import lavahack.client.utils.client.interfaces.impl.StateAnimator
import lavahack.client.utils.render.screen.resetScissor
import lavahack.client.utils.render.screen.setScissor
import net.minecraft.client.gui.DrawContext

interface IContainable {
    var open : Boolean
    var components : MutableList<IComponent>
    val fullHeight : Double
}

interface IComponent {
    val context : IComponentContext
    var width : Double
    var height : Pair<Double, Double>

    //TODO: rename it!
    fun handleRender(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        preRenderTick()
        render(context, mouseX, mouseY)
        postRenderTick()
    }

    fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) { }

    fun update(
        updateX : Number,
        updateY : Number
    ) { }

    fun preRenderTick() { }

    fun postRenderTick() { }

    fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) { }

    fun mouseReleased(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) { }

    fun mouseDragged(
        mouseX : Double,
        mouseY : Double,
        button : Int,
        deltaX : Double,
        deltaY : Double
    ) { }

    fun keyPressed(
        code : Int,
        scan : Int,
        modifiers : Int
    ) { }

    fun charTyped(
        char : Char,
        modifiers : Int
    ) { }

    fun hovering(
        mouseX : Double,
        mouseY : Double
    ) = (context.x..(context.x+ width)).contains(mouseX) && (context.y..(context.y + height.first)).contains(mouseY)

    fun width() = width

    fun visible() = true
}

abstract class Component(
    override val context : IComponentContext
) : IComponent {
    override var width
        get() = ModuleGui.modifyWidth(context.layer, ModuleGui.WIDTH).toDouble()
        set(value) { }

    override var height
        get() = Pair(ModuleGui.HEIGHT.value.toDouble(), ModuleGui.HEIGHT.value.toDouble())
        set(value) { }

    var endX : Double? = null
    var endY : Double? = null

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        ModuleGui.backgrounds(context, this, mouseX, mouseY)
        ModuleGui.lines(context, this)
        ModuleGui.outline(context, this)
    }

    override fun update(
        updateX : Number,
        updateY : Number
    ) {
        if(ModuleGui.FRAME_ANIMATION_STATE.value) {
            endX = updateX.toDouble()
            endY = updateY.toDouble()
        } else {
            context.x = updateX.toDouble()
            context.y = updateY.toDouble()
            endX = null
            endY = null
        }
    }

    override fun preRenderTick() {
        //TODO: move it somewhere
        if(ModuleGui.FRAME_ANIMATION_STATE.value && (this.context.x != endX || this.context.y != endY) && endX != null) {
            this.context.x = animate(this.context.x, endX!!, ModuleGui.FRAME_ANIMATION_SPEED.value)
            this.context.y = animate(this.context.y, endY!!, ModuleGui.FRAME_ANIMATION_SPEED.value)
        }
    }

    override fun hovering(
        mouseX : Double,
        mouseY : Double
    ) = mouseX >= context.x + ModuleGui.OFFSETS_X.value && mouseX <= context.x + width - ModuleGui.OFFSETS_X.value && mouseY >= context.y + context.offset + ModuleGui.OFFSETS_Y.value && mouseY <= context.y + context.offset + height.first - ModuleGui.OFFSETS_Y.value

    override fun width() = width

    override fun visible() = context.visible.visible()
}

abstract class ContainableComponent(
    context : IComponentContext,
    private val btn : Int = 1,
    protected var onClick : (Int) -> Unit = {  }
) : Component(
    context
), IContainable {
    override var components = mutableListOf<IComponent>()
    private val openAnimator = Animator2(ModuleGui.CONTAINER_ANIMATOR, 0.0, 1.0)

    //TODO: rename
    protected open var realOpen = false

    override var open : Boolean = false
        get() {
            //TODO: optimize
            return if(ModuleGui.CONTAINER_ANIMATION_STATE.value) openAnimator.get() != 0.0 || field else field
        }
        set(value) {
            field = value
            realOpen = value
        }

    override val fullHeight : Double
        get() = if(components.isNotEmpty()) {
            val coeff = if(ModuleGui.CONTAINER_ANIMATION_STATE.value) openAnimator.get() else if(open) 1.0 else 0.0
            var realHeight = 0.0

            if(coeff != 0.0) {
                for(component in components) {
                    if(component.visible()) {
                        realHeight += component.height.first

                        if(component is IContainable) {
                            realHeight += component.fullHeight
                        }
                    }
                }
            }

            //TODO: add just realHeight without coeff
            realHeight * coeff
        } else {
            0.0
        }

    //TODO: rename it!
    private var needsScissor = false
    private var coeff = 0.0

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        super.render(context, mouseX, mouseY)

        if(coeff != 0.0) {
            for(component in components) {
                if(component.visible()) {
                    component.handleRender(context, mouseX, mouseY)
                }
            }
        }
    }

    override fun preRenderTick() {
        super.preRenderTick()
        //TODO: move to mouseClicked method?
        openAnimator.reverse(realOpen)
        openAnimator.update()

        coeff = if(ModuleGui.CONTAINER_ANIMATION_STATE.value) openAnimator.get() else if(open) 1.0 else 0.0

        needsScissor = coeff != 0.0 && coeff != 1.0

        if(needsScissor) {
            setScissor(
                this.context.x,
                this.context.y + this.context.offset,
                width,
                fullHeight + height.first
            )
        }
    }

    override fun postRenderTick() {
        super.postRenderTick()

        //TODO: there may be conflict cuz of many scissors
        if(needsScissor) {
            resetScissor()
        }
    }

    override fun update(
        updateX : Number,
        updateY : Number
    ) {
        super.update(updateX, updateY)

        for (component in components) {
            component.update(updateX, updateY)
        }
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        if(open) {
            for(component in components) {
                if(component.visible()) {
                    component.mouseClicked(mouseX, mouseY, button)
                }
            }
        }

        if(hovering(mouseX, mouseY)) {
            onClick(button)

            if(button == btn) {
                open = !open
            }
        }
    }

    override fun mouseReleased(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseReleased(mouseX, mouseY, button)

        if(open) {
            for(component in components) {
                if(component.visible()) {
                    component.mouseReleased(mouseX, mouseY, button)
                }
            }
        }
    }

    override fun mouseDragged(
        mouseX : Double,
        mouseY : Double,
        button : Int,
        deltaX : Double,
        deltaY : Double
    ) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)

        if(open) {
            for(component in components) {
                if(component.visible()) {
                    component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                }
            }
        }
    }

    override fun keyPressed(
        code : Int,
        scan : Int,
        modifiers : Int
    ) {
        super.keyPressed(code, scan, modifiers)

        if(open) {
            for(component in components) {
                if(component.visible()) {
                    component.keyPressed(code, scan, modifiers)
                }
            }
        }
    }

    override fun charTyped(
        char : Char,
        modifiers : Int
    ) {
        super.charTyped(char, modifiers)

        if(open) {
            for(component in components) {
                if(component.visible()) {
                    component.charTyped(char, modifiers)
                }
            }
        }
    }
}

abstract class ToggleableComponent(
    context : IComponentContext,
    _stateSupplier : (() -> Boolean)?,
    protected var toggleCallback : (Boolean) -> Unit,
    private val toggleable : Boolean = true
) : ContainableComponent(
    context
) {
    protected var state = false

    val stateSupplier : () -> Boolean = _stateSupplier ?: { state }.also {
        state = it()
    }

    val animator = StateAnimator(ModuleGui.TOGGLE_ANIMATOR)

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        if(hovering(mouseX, mouseY) && button == 0 && toggleable) {
            state = !stateSupplier()
            toggleCallback(state)
        }
    }
}