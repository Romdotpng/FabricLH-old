package lavahack.client.features.gui.selectionbar.components

import lavahack.client.features.gui.selectionbar.SelectionBar
import lavahack.client.features.subsystem.subsystems.SelectedScreenManager

import lavahack.client.utils.beginScreen
import lavahack.client.utils.drawScreen
import lavahack.client.utils.endScreen
import lavahack.client.utils.mc
import lavahack.client.utils.render.screen.fontHeight
import lavahack.client.utils.render.screen.outlineRectWH
import lavahack.client.utils.render.screen.rectWH
import net.minecraft.client.gui.DrawContext

@Suppress("PropertyName")
class Canvas {
    val BUTTONS = mutableMapOf<Int, Button>()

    init {
        for((index, screens) in SelectedScreenManager.SELECTABLE_SCREENS.entries) {
            val screen = screens.first
            val button = Button(index, screen)

            BUTTONS[index] = button
        }
    }

    fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) {
        var canvasWidth = 0.0

        for(button in BUTTONS.values) {
            val buttonWidth = button.width

            canvasWidth += buttonWidth
        }

        val canvasX = mc.window.scaledWidth / 2.0 - canvasWidth / 2.0
        val canvasHeight = fontHeight() + SelectionBar.OFFSET.value * 2

        if(SelectionBar.BACKGROUND.value) {
            if(SelectionBar.SHADERED_BACKGROUND.value) {
                SelectionBar.SHADER.beginScreen()
            }

            rectWH(
                context,
                canvasX,
                0.0,
                canvasWidth,
                canvasHeight,
                SelectionBar.BACKGROUND_COLOR.value
            )

            if(SelectionBar.SHADERED_BACKGROUND.value) {
                SelectionBar.SHADER.endScreen()
            }
        }

        var buttonOffsetX = 0.0

        for(button in BUTTONS.values) {
            button.update(canvasX + buttonOffsetX, 0.0)
            button.render(context, canvasX, mouseX, mouseY, delta)

            buttonOffsetX += button.width
        }

        if(SelectionBar.OUTLINE.value) {
            SelectionBar.SHADER.drawScreen {
                outlineRectWH(
                    context,
                    canvasX,
                    0.0,
                    canvasWidth,
                    canvasHeight,
                    SelectionBar.PRIMARY_COLOR.value.clone().alpha(255),
                    1f
                )
            }
        }
    }

    fun mouseClicked(
        mouseX : Double,
        mouseY : Double
    ) : Boolean {
        for(button in BUTTONS.values) {
            if(button.mouseClicked(mouseX, mouseY)) {
                return true
            }
        }

        return false
    }
}