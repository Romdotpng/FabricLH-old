package lavahack.client.utils.client.enums

import lavahack.client.features.gui.huds.HudEditor
import lavahack.client.utils.client.interfaces.IRect
import lavahack.client.utils.mc

enum class HudAnchors(
    val pos : () -> Pair<Int, Int>,
    val corrector : (IRect, Float) -> Unit,
    val draggables : MutableSet<IRect> = mutableSetOf()
) {
    None(
        { mc.window.scaledWidth to mc.window.scaledHeight },
        { rect, _ ->
            rect.x = rect.x.coerceIn(0f..mc.window.scaledWidth.toFloat())
            rect.y = rect.y.coerceIn(0f..mc.window.scaledHeight.toFloat())
        }
    ),
    LeftTop(
        { 0 to 0 },
        { draggable, height ->
            draggable.x = 0f
            draggable.y = height
        }
    ),
    LeftBottom(
        { 0 to mc.window.scaledHeight - HudEditor.ANCHOR_SIZE.value },
        { draggable, height ->
            draggable.x = 0f
            draggable.y = mc.window.scaledHeight - height - draggable.h
        }
    ),
    RightTop(
        { mc.window.scaledWidth - HudEditor.ANCHOR_SIZE.value to 0 },
        { draggable, height ->
            draggable.x = mc.window.scaledWidth - draggable.w
            draggable.y = height
        }
    ),
    RightBottom(
        { mc.window.scaledWidth - HudEditor.ANCHOR_SIZE.value to mc.window.scaledHeight - HudEditor.ANCHOR_SIZE.value },
        { draggable, height ->
            draggable.x = mc.window.scaledWidth - draggable.w
            draggable.y = mc.window.scaledHeight - height - draggable.h
        }
    ),
    MiddleTop(
        { mc.window.scaledWidth / 2 - HudEditor.ANCHOR_SIZE.value / 2 to 0 },
        { draggable, height ->
            draggable.x = mc.window.scaledWidth / 2 - draggable.w / 2
            draggable.y = height
        }
    )
}