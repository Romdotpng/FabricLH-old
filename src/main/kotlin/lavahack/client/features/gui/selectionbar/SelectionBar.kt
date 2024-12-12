package lavahack.client.features.gui.selectionbar

import lavahack.client.features.gui.LavaHackScreen
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.searchbar.SearchBar
import lavahack.client.features.gui.selectionbar.components.Button
import lavahack.client.features.gui.selectionbar.components.Canvas
import lavahack.client.features.subsystem.subsystems.SelectedScreenManager
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Animator2
import lavahack.client.utils.Colour
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.AnimatorContext
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.mc
import lavahack.client.utils.or
import net.minecraft.client.gui.DrawContext
import org.lwjgl.glfw.GLFW

object SelectionBar : LavaHackScreen(
    "Selection Bar"
) {
    private val SYNC_WITH_GUI = register(Setting("Sync With Gui", false))

    private val COLORS_GROUP = register(SettingGroup("Colors").link(SYNC_WITH_GUI))
    val PRIMARY_COLOR = register(COLORS_GROUP.add(Setting("Primary Color", Colour(255, 0, 0, 255), "Primary").link(SYNC_WITH_GUI, ModuleGui.PRIMARY_COLOR)))
    val BACKGROUND_COLOR = register(COLORS_GROUP.add(Setting("Background Color", Colour(0, 0, 0, 120), "Background").link(SYNC_WITH_GUI, ModuleGui.BACKGROUND_COLOR)))
    val PRIMARY_TEXT_COLOR = register(COLORS_GROUP.add(Setting("Primary Text Color", Colour(255, 255, 255, 255), "Primary Text").link(SYNC_WITH_GUI, ModuleGui.PRIMARY_TEXT_COLOR)))
    val BACKGROUND_TEXT_COLOR = register(COLORS_GROUP.add(Setting("Background Text Color", Colour(255, 255, 255, 255), "Background Text").link(SYNC_WITH_GUI, ModuleGui.BACKGROUND_TEXT_COLOR)))

    val BACKGROUND = register(Setting("Background", true).link(SYNC_WITH_GUI, ModuleGui.BACKGROUND1))
    val SHADERED_BACKGROUND = register(Setting("Shadered Background", false).link(SYNC_WITH_GUI, ModuleGui.SHADERED_BACKGROUNDS))
    val OUTLINE = register(Setting("Outline", false).link(SYNC_WITH_GUI, ModuleGui.VERTICAL_LINES or ModuleGui.HORIZONTAL_LINES))
    val OFFSET = register(SettingNumber("Offset", 5.0, 0.0..5.0))

    private val ANIMATIONS_GROUP = register(SettingGroup("Animations"))
    val ANIMATION_STATE = register(ANIMATIONS_GROUP.add(Setting("State", false)))
    private val ANIMATION_EASING = register(ANIMATIONS_GROUP.add(SettingEnum("Easing", Easings.Linear)))
    private val ANIMATION_LENGTH = register(ANIMATIONS_GROUP.add(SettingNumber("Length", 750L, 0L..1000L)))

    private val SHADERS_GROUP = register(SettingGroup("Shaders"))
    val SHADER = register(SHADERS_GROUP.add(SettingEnum("Shader", CoreShaders.None)))

    val CANVAS = Canvas()

    private val ANIMATOR_CONTEXT = AnimatorContext(ANIMATION_EASING, ANIMATION_LENGTH)
    val ANIMATOR = Animator2(ANIMATOR_CONTEXT, 0.0, 1.0)

    var PREV_SELECTED_BUTTON : Button? = null

    override fun render(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float
    ) {
        SelectedScreenManager.SELECTED_SCREEN.second.render(context, mouseX, mouseY, delta)

        CANVAS.render(context, mouseX, mouseY, delta)

        if(SelectedScreenManager.SELECTED_SCREEN.first.needsSearchBar) {
            SearchBar.render(context, mouseX, mouseY, delta)
        }
    }

    override fun mouseClicked(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) = true.also {
        if(!CANVAS.mouseClicked(mouseX, mouseY) && (!SelectedScreenManager.SELECTED_SCREEN.first.needsSearchBar || !SearchBar.mouseClicked(mouseX, mouseY, button))) {
            SelectedScreenManager.SELECTED_SCREEN.second.mouseClicked(mouseX, mouseY, button)
        }
    }

    override fun mouseReleased(
        mouseX : Double,
        mouseY : Double,
        button : Int
    ) = SelectedScreenManager.SELECTED_SCREEN.second.mouseReleased(mouseX, mouseY, button)

    override fun mouseDragged(
        mouseX : Double,
        mouseY : Double,
        button : Int,
        deltaX : Double,
        deltaY : Double
    ) = SelectedScreenManager.SELECTED_SCREEN.second.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)

    override fun mouseScrolled(
        mouseX : Double,
        mouseY : Double,
        amount : Double,
    ) = SelectedScreenManager.SELECTED_SCREEN.second.mouseScrolled(mouseX, mouseY, amount)

    override fun keyPressed(
        keyCode : Int,
        scanCode : Int,
        modifiers : Int
    ) = if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
        mc.setScreen(null)

        true
    } else {
        if(SelectedScreenManager.SELECTED_SCREEN.first.needsSearchBar) {
            SearchBar.keyPressed(keyCode, scanCode, modifiers)
        }

        SelectedScreenManager.SELECTED_SCREEN.second.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(
        char : Char,
        modifiers : Int
    ) = true.also {
        if(SelectedScreenManager.SELECTED_SCREEN.first.needsSearchBar) {
            SearchBar.charTyped(char, modifiers)
        }

        SelectedScreenManager.SELECTED_SCREEN.second.charTyped(char, modifiers)
    }

    override fun onOpen() {
        ANIMATOR.reset()
        PREV_SELECTED_BUTTON = null

        SelectedScreenManager.SELECTED_SCREEN.first.onOpen()
    }

    override fun keyReleased(
        keyCode : Int,
        scanCode : Int,
        modifiers : Int
    ) = SelectedScreenManager.SELECTED_SCREEN.second.keyReleased(keyCode, scanCode, modifiers)
}