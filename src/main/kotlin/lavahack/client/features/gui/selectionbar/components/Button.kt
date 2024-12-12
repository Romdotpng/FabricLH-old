package lavahack.client.features.gui.selectionbar.components

import lavahack.client.features.gui.LavaHackScreen
import lavahack.client.features.gui.selectionbar.SelectionBar
import lavahack.client.features.subsystem.subsystems.SelectedScreenManager
import lavahack.client.utils.drawScreen
import lavahack.client.utils.render.screen.drawString
import lavahack.client.utils.render.screen.fontHeight
import lavahack.client.utils.render.screen.rectWH
import lavahack.client.utils.render.screen.stringWidth
import net.minecraft.client.gui.DrawContext
import kotlin.math.absoluteValue

class Button(
    val index : Int,
    private val screen : LavaHackScreen
) {
    var x = 0.0
    var y = 0.0

    val width get() = stringWidth(screen.title.string) + SelectionBar.OFFSET.value * 2

    val height get() = fontHeight() + SelectionBar.OFFSET.value * 2.0

    fun render(
        context : DrawContext,
        canvasX : Double,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) {
        val title = screen.title.string
        val selected = SelectedScreenManager.SELECTED_SCREEN_INDEX == index

        if(selected) {
            val buttonX = if(SelectionBar.PREV_SELECTED_BUTTON == null || SelectionBar.PREV_SELECTED_BUTTON == this || !SelectionBar.ANIMATION_STATE.value) {
                x
            } else {
                SelectionBar.ANIMATOR.update()

                val diffX = x - SelectionBar.PREV_SELECTED_BUTTON!!.x
                var coeff = SelectionBar.ANIMATOR.get()

                if(diffX < 0) {
                    coeff = 1.0 - coeff
                }

                //TODO: rewrite this shit
                canvasX + diffX.absoluteValue * coeff + if((index > 0 && index < SelectedScreenManager.SELECTABLE_SCREENS.size)) (if(SelectionBar.PREV_SELECTED_BUTTON!!.index != SelectedScreenManager.SELECTABLE_SCREENS.size - 1) SelectionBar.PREV_SELECTED_BUTTON!!.x else x) - canvasX else 0.0
            }


            SelectionBar.SHADER.drawScreen {
                rectWH(
                    context,
                    buttonX,
                    y,
                    width,
                    height,
                    SelectionBar.PRIMARY_COLOR.value
                )
            }
        }

        drawString(
            context,
            title,
            x + SelectionBar.OFFSET.value,
            y + SelectionBar.OFFSET.value,
            if(selected) SelectionBar.PRIMARY_TEXT_COLOR.value
            else SelectionBar.BACKGROUND_TEXT_COLOR.value,
            shadow = true
        )
    }

    fun update(
        x : Double,
        y : Double
    ) {
        this.x = x
        this.y = y
    }

    fun mouseClicked(
        mouseX : Double,
        mouseY : Double
    ) = if(mouseX in x..(x + width) && mouseY in y..(y + height)) {
        if(SelectedScreenManager.SELECTED_SCREEN_INDEX != index) {
            val prevButton = SelectionBar.CANVAS.BUTTONS[SelectedScreenManager.SELECTED_SCREEN_INDEX]!!
            val prevScreen = SelectedScreenManager.SELECTABLE_SCREENS[SelectedScreenManager.SELECTED_SCREEN_INDEX]!!.first
            val currentScreen = SelectedScreenManager.SELECTABLE_SCREENS[index]!!.first

            prevScreen.onClose()
            currentScreen.onOpen()

            SelectionBar.ANIMATOR.reset()
            SelectionBar.PREV_SELECTED_BUTTON = prevButton
        }

        SelectedScreenManager.SELECTED_SCREEN_INDEX = index

        true
    } else {
        false
    }
}