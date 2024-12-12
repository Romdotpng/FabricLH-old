package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.gui.configs.ConfigGui
import lavahack.client.features.gui.huds.HudEditor
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.subsystem.SubSystem

object SelectedScreenManager : SubSystem(
    "Selected Screen Manager"
) {
     /**
     *
     * INDEX to (GUI to BASE)
     *
     * INDEX is index of screen
     *
     * GUI is open/close callbacks provider of screen
     *
     * BASE is renderer of screen
     *
     */
    val SELECTABLE_SCREENS = mutableMapOf(
        0 to (ModuleGui to ModuleGui),
        1 to (HudEditor to ModuleGui),
        2 to (ConfigGui to ModuleGui)
    )

    var SELECTED_SCREEN_INDEX = 0

    val SELECTED_SCREEN get() = SELECTABLE_SCREENS[SELECTED_SCREEN_INDEX] ?: SELECTABLE_SCREENS[0]!!
}
