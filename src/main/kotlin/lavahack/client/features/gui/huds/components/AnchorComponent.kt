package lavahack.client.features.gui.huds.components

import lavahack.client.features.gui.huds.HudEditor
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.EmptyContext
import lavahack.client.features.gui.modules.component.IComponent
import lavahack.client.utils.Animator2
import lavahack.client.utils.client.enums.HudAnchors
import lavahack.client.utils.render.screen.rectWH
import net.minecraft.client.gui.DrawContext

class AnchorComponent(
    private val anchor : HudAnchors
) : IComponent {
    override val context = EmptyContext()
    override var width = 0.0
    override var height = 0.0 to 0.0

    private val animator = Animator2(HudEditor.ANCHOR_ANIMATOR_CONTEXT, 0.0, 1.0)

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int
    ) {
        ModuleGui.addNormalRender2 {
            animator.reverse(HudEditor.DRAGGING_DRAGGABLES.isNotEmpty())
            animator.update()

            val pos = anchor.pos()
            val alpha = (animator.get() * HudEditor.ANCHOR_COLOR.value.alpha).toInt()

            rectWH(
                context,
                pos.first,
                pos.second,
                HudEditor.ANCHOR_SIZE.value,
                HudEditor.ANCHOR_SIZE.value,
                HudEditor.ANCHOR_COLOR.value.clone().alpha(alpha)
            )
        }
    }

    override fun visible() = HudEditor.ANCHOR_SHOW.value
}