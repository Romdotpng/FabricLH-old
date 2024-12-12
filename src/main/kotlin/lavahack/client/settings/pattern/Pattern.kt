package lavahack.client.settings.pattern

import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.SettingRegistry

/**
 * @author _kisman_
 * @since 10:18 of 26.05.2023
 */
abstract class Pattern : ISettingRegistry {
    override val registry = SettingRegistry()
}