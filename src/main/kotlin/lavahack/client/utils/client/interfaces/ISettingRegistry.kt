package lavahack.client.utils.client.interfaces

import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.SettingRegistry

/**
 * @author _kisman_
 * @since 12:52 of 15.05.2023
 */
interface ISettingRegistry : Collection<Setting<*>> {
    val registry : SettingRegistry

    //TODO: move it into separate class
    override val size get() = registry.settings.size

    override fun isEmpty() = registry.settings.isEmpty()

    override fun contains(
        element : Setting<*>
    ) = registry.settings.contains(element)

    override fun iterator() = registry.settings.iterator()

    override fun containsAll(
        elements : Collection<Setting<*>>
    ) = registry.settings.containsAll(elements)
}