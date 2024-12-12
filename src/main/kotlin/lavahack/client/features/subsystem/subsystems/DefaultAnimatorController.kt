package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.interfaces.impl.AnimatorContext
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 15:53 of 04.07.2023
 */
object DefaultAnimatorController : SubSystem(
    "Default Animator Controller"
) {
    val DEFAULT_EASING = register(SettingEnum("Easing", Easings.Linear))
    val DEFAULT_LENGTH = register(SettingNumber("Length", 750L, 100L..1000L))

    val DEFAULT_ANIMATOR_CONTEXT = AnimatorContext(DEFAULT_EASING, DEFAULT_LENGTH)
}