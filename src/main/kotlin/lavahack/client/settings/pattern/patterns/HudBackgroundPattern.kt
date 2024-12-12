package lavahack.client.settings.pattern.patterns

import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.Pattern
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.render.screen.outlineRectWH
import lavahack.client.utils.render.screen.rectWH
import net.minecraft.client.gui.DrawContext

@Suppress("PrivatePropertyName")
class HudBackgroundPattern : Pattern() {
    private val GROUP = register(SettingGroup("Background"))
    private val FILL = register(GROUP.add(Setting("Fill", false)))
    private val OUTLINE = register(GROUP.add(Setting("Outline", false)))
    private val FILL_COLOR = register(GROUP.add(Setting("Fill Color", Colour(0, 0, 0, 120))))
    private val OUTLINE_COLOR = register(GROUP.add(Setting("Outline Color", Colour(-1))))

    init {
        GROUP.prefix("Background")
    }

    fun render(
        context : DrawContext,
        x : Number,
        y : Number,
        w : Number,
        h : Number
    ) {
        if(FILL.value) {
            rectWH(
                context,
                x,
                y,
                w,
                h,
                FILL_COLOR.value
            )
        }

        if(OUTLINE.value) {
            outlineRectWH(
                context,
                x,
                y,
                w,
                h,
                OUTLINE_COLOR.value,
                1
            )
        }
    }
}