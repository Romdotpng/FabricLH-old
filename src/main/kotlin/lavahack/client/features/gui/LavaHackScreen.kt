package lavahack.client.features.gui

import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.SettingRegistry
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 * @author _kisman_
 * @since 18:19 of 09.05.2023
 */
open class LavaHackScreen(
    name : String,
    val needsSearchBar : Boolean = false,
    val needsBackground : Boolean = true
) : Screen(
    Text.literal(name)
), ISettingRegistry {
    override val registry = SettingRegistry()

    var mouseX = 0
    var mouseY = 0

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) {
        this.mouseX = mouseX
        this.mouseY = mouseY

        super.render(context, mouseX, mouseY, delta)
        onRender(context, mouseX, mouseY, delta)
    }

    override fun onDisplayed() {
        super.onDisplayed()

        onOpen()
    }

    override fun removed() {
        super.removed()

        onClose()
    }

    open fun onRender(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) { }

    open fun onOpen() { }

    open fun onClose() { }
}