package lavahack.client.utils.client.interfaces.impl

import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.interfaces.IAnimatorContext

open class AnimatorContext(
    private val easingSetting : SettingEnum<Easings>,
    private val lengthSetting : Setting<Long>
) : IAnimatorContext {
    override val easing : Easings
        get() = easingSetting.valEnum

    override val length : Long
        get() = lengthSetting.value
}