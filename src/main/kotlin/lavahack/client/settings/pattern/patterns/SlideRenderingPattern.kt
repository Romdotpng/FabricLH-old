package lavahack.client.settings.pattern.patterns

import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.ProgressBoxModifiers
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 12:51 of 06.07.2023
 */
@Suppress("PrivatePropertyName", "PropertyName")
class SlideRenderingPattern : BoxRenderingPattern() {
    private val SLIDE_GROUP = register(SettingGroup("Slide"))

    private val LENGTHS_GROUP = register(SLIDE_GROUP.add(SettingGroup("Lengths")))

    val MOVING_LENGTH = register(LENGTHS_GROUP.add(SettingNumber("Moving", 0L, 0L..1000L)))
    val FADE_LENGTH = register(LENGTHS_GROUP.add(SettingNumber("Fade", 0L, 0L..1000L)))

    private val EASINGS_GROUP = register(SLIDE_GROUP.add(SettingGroup("Easings")))

    val MOVING_EASING = register(EASINGS_GROUP.add(SettingEnum("Moving", Easings.Linear)))
    val FADE_EASING = register(EASINGS_GROUP.add(SettingEnum("Fade", Easings.Linear)))

    private val FADE_GROUP = register(SLIDE_GROUP.add(SettingGroup("Fade")))

    val ALPHA_FADE = register(FADE_GROUP.add(Setting("Alpha", false)))
    val BOX_FADE = register(FADE_GROUP.add(Setting("Box", false)))
    val BOX_FADE_LOGIC = register(FADE_GROUP.add(SettingEnum("Box Logic", ProgressBoxModifiers.CentredBox)))

    init {
        SLIDE_GROUP.prefix("Slide")
        LENGTHS_GROUP.prefix("Lengths")
        EASINGS_GROUP.prefix("Easings")
        FADE_GROUP.prefix("Fade")
    }
}