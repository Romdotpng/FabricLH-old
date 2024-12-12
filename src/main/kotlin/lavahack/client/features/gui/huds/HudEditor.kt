package lavahack.client.features.gui.huds

import lavahack.client.features.gui.LavaHackScreen
import lavahack.client.features.gui.huds.components.AnchorComponent
import lavahack.client.features.gui.modules.Frame
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.huds.components.DraggableComponent
import lavahack.client.features.hud.Huds
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Colour
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.HudAnchors
import lavahack.client.utils.client.interfaces.IPrefixable
import lavahack.client.utils.client.interfaces.impl.AnimatorContext
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 13:54 of 12.05.2023
 */
object HudEditor : LavaHackScreen(
    "Hud Editor",
    true,
    false
), IPrefixable {
    val BOX_COLOR = register(Setting("Box Color", Colour(0, 0, 0, 120)))

    private val ANCHOR_GROUP = register(SettingGroup("Anchors"))
    val ANCHOR_SHOW = register(ANCHOR_GROUP.add(Setting("Show", true)))
    val ANCHOR_SIZE = register(ANCHOR_GROUP.add(SettingNumber("Size", 30, 10..100)))
    val ANCHOR_COLOR = register(ANCHOR_GROUP.add(Setting("Color", Colour(0, 0, 0, 120))))
    private val ANCHOR_LENGTH = register(ANCHOR_GROUP.add(SettingNumber("Length", 1000L, 0L..1000L)))
    private val ANCHOR_EASING = register(ANCHOR_GROUP.add(SettingEnum("Easing", Easings.Linear)))

    private val SELECTION_HIGHLIGHT_GROUP = register(SettingGroup("Selection Highlight"))
    val SELECTION_HIGHLIGHT_STATE = register(SELECTION_HIGHLIGHT_GROUP.add(Setting("State", false)))
    val SELECTION_HIGHLIGHT_COLOR = register(SELECTION_HIGHLIGHT_GROUP.add(Setting("Color", Colour(-1))))
    private val SELECTION_HIGHLIGHT_LENGTH = register(SELECTION_HIGHLIGHT_GROUP.add(SettingNumber("Length", 1000L, 0L..1000L)))
    private val SELECTION_HIGHLIGHT_EASING = register(SELECTION_HIGHLIGHT_GROUP.add(SettingEnum("Easing", Easings.Linear)))

    val ANCHOR_ANIMATOR_CONTEXT = AnimatorContext(ANCHOR_EASING, ANCHOR_LENGTH)
    val SELECTION_HIGHLIGHT_ANIMATOR_CONTEXT = AnimatorContext(SELECTION_HIGHLIGHT_EASING, SELECTION_HIGHLIGHT_LENGTH)

    val HUD_FRAMES = mutableListOf<Frame>()

    val DRAGGING_DRAGGABLES = mutableListOf<DraggableComponent>()

    init {
        ANCHOR_GROUP.prefix("Anchor")
        SELECTION_HIGHLIGHT_GROUP.prefix("Selection Highlight")
    }

    fun create() {
        val settingsFrame = Frame(emptyList(), "Settings", 20.0 + ModuleGui.WIDTH, 10.0)

        HUD_FRAMES.add(Frame(Huds.huds, "Hud Editor", 10.0, 10.0))
        HUD_FRAMES.add(settingsFrame)

        ModuleGui.addSettingComponents(settingsFrame, registry.settings, layerOffset = -1) { it.bound == this }

        for(anchor in HudAnchors.values()) {
            val component = AnchorComponent(anchor)

            settingsFrame.components.add(component)
        }
    }

    override fun onOpen() {
        ModuleGui.frames = HUD_FRAMES
    }

    override fun prefix() = "hudeditor"
}