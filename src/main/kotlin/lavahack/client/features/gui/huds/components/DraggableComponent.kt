package lavahack.client.features.gui.huds.components

import lavahack.client.features.gui.huds.HudEditor
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.DraggableContext
import lavahack.client.features.gui.modules.component.IComponent
import lavahack.client.utils.Animator2
import lavahack.client.utils.client.enums.HudAnchors
import lavahack.client.utils.client.interfaces.FakeThing
import lavahack.client.utils.client.interfaces.IRect
import lavahack.client.utils.render.screen.outlineRectWH
import net.minecraft.client.gui.DrawContext

/**
 * @author _kisman_
 * @since 11:02 of 24.05.2023
 */
@Suppress("UNUSED_PARAMETER")
class DraggableComponent(
    private val draggable : IRect,
) : IComponent, FakeThing {
    override val context = DraggableContext(draggable)

    override var width : Double
        get() = draggable.w.toDouble()
        set(value) {}

    override var height : Pair<Double, Double>
        get() = draggable.h.toDouble() to draggable.h.toDouble()
        set(value) {}

    private var dragging = false
    private val animator = Animator2(HudEditor.SELECTION_HIGHLIGHT_ANIMATOR_CONTEXT, 0.0, 1.0)

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        ModuleGui.addNormalRender {
            animator.reverse(dragging)
            animator.update()

            val alpha = (animator.get() * HudEditor.SELECTION_HIGHLIGHT_COLOR.value.alpha).toInt()

            ModuleGui.rect(
                context,
                this,
                HudEditor.BOX_COLOR.value,
                false
            )

            if(HudEditor.SELECTION_HIGHLIGHT_STATE.value) {
                outlineRectWH(
                    context,
                    draggable.x - 1f,
                    draggable.y - 1f,
                    draggable.w + 2f,
                    draggable.h + 2f,
                    HudEditor.SELECTION_HIGHLIGHT_COLOR.value.clone().alpha(alpha),
                    1
                )
            }
        }
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseClicked(mouseX, mouseY, button)

        if(hovering(mouseX, mouseY) && button == 0) {
            dragging = true

            HudEditor.DRAGGING_DRAGGABLES.add(this)
        }
    }

    override fun mouseReleased(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) {
        super.mouseReleased(mouseX, mouseY, button)

        HudEditor.DRAGGING_DRAGGABLES.remove(this)

        if(dragging) {
            for(anchor in HudAnchors.values()) {
                if(anchor == HudAnchors.None) {
                    continue
                }

                val pos = anchor.pos()
                val x = pos.first
                val y = pos.second
                val size = HudEditor.ANCHOR_SIZE.value

                if(ModuleGui.mouseX >= x && ModuleGui.mouseX <= x + size && ModuleGui.mouseY >= y && ModuleGui.mouseY <= y + size) {
                    if(!anchor.draggables.contains(draggable)) {
                        anchor.draggables.add(draggable)
//                        draggable.bound = anchor
                    }
                } else {
                    /*if(draggable.bound == anchor) {
                        draggable.bound = HudAnchors.None
                    }*/

                    if(anchor.draggables.contains(draggable)) {
                        HudAnchors.None.draggables.add(draggable)
                    }

                    anchor.draggables.remove(draggable)

                }
            }
        }

        dragging = false
    }

    override fun mouseDragged(
        mouseX : Double,
        mouseY : Double,
        button : Int,
        deltaX : Double,
        deltaY : Double
    ) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)

        if(dragging) {
            draggable.x += deltaX.toFloat()
            draggable.y += deltaY.toFloat()
            draggable.correct()

            /*for(hud in Huds.huds) {
                val hitbox = hud.HITBOX

                if(draggable != hitbox && draggable.intersects(hitbox)) {
                    println("linking to ${hud.info.name}")
                    if(hitbox.anchored != null) {
                        //anchor - тот к которому мы привязаны
                        //anchored - тот который к нам привязан
                        draggable.anchored = hitbox.anchored
                        hitbox.anchored!!.anchor = draggable
                    }
                    draggable.anchor = hitbox
                    hitbox.anchored = draggable

                    dragging = false

                    break
                }
            }

            if(draggable.anchor != null && !draggable.intersects(draggable.anchor!!)) {
                if(draggable.anchored != null) {
                    draggable.anchored!!.anchor = draggable.anchor
                }

                draggable.anchor!!.anchored = draggable.anchor
            }*/
        }
    }
}