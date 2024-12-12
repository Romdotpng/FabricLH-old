package lavahack.client.features.subsystem

import lavahack.client.utils.client.interfaces.IListenerRegistry
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.ListenerRegistry
import lavahack.client.utils.client.interfaces.impl.SettingRegistry

/**
 * @author _kisman_
 * @since 3:47 of 08.05.2023
 */
abstract class SubSystem(
    val name : String,
    val hasPreinitializer : Boolean = false
) : ISettingRegistry, IListenerRegistry {
    override val registry = SettingRegistry()
    override val listeners = ListenerRegistry()

    open fun preinit() { }
    open fun init() { }
}