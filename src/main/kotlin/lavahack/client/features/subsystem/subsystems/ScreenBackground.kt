package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.ScreenEvent
import lavahack.client.features.gui.LavaHackScreen
import lavahack.client.features.gui.selectionbar.SelectionBar
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.DEFAULT_BACKGROUND_COLOR
import lavahack.client.utils.client.enums.ColorerModes
import lavahack.client.utils.client.interfaces.impl.RectColorer
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.mc
import lavahack.client.utils.render.screen.gradientRect
import java.awt.Color

object ScreenBackground : SubSystem(
    "Screen Background"
) {
    val STATE = register(Setting("State", false))
    val MODE = register(SettingEnum("Mode", ColorerModes.Single))
    val COLORER = register(RectColorer())

    //TODO: change to init() method
    init {
        register(Setting("Reset Color", false) {
            if(it.value) {
                COLORER.set(Color(DEFAULT_BACKGROUND_COLOR))

                it.value = false
            }
        })

        listener<ScreenEvent.Render.Pre>(1) {
            val screen = it.screen

            if(mc.world != null && (screen !is LavaHackScreen || if(screen is SelectionBar) SelectedScreenManager.SELECTED_SCREEN.first.needsBackground else screen.needsBackground)) {
                if(STATE.value) {
                    val colorer = COLORER.apply(MODE.valEnum)

                    gradientRect(
                        it.context,
                        0,
                        0,
                        mc.window.scaledWidth,
                        mc.window.scaledHeight,
                        colorer.color1(),
                        colorer.color2(),
                        colorer.color3(),
                        colorer.color4()
                    )
                } else {
                    it.screen.renderBackground(it.context)
                }
            }
        }
    }
}