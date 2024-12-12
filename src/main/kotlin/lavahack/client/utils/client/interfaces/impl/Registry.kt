package lavahack.client.utils.client.interfaces.impl

import lavahack.client.utils.client.interfaces.ICallbackRegistry
import lavahack.client.utils.client.interfaces.IListenerRegistry
import lavahack.client.utils.client.interfaces.ISettingRegistry

/**
 * @author _kisman_
 * @since 12:49 of 25.06.2023
 */
open class Registry : ISettingRegistry, ICallbackRegistry, IListenerRegistry {
    override val registry = SettingRegistry()
    override val callbacks = CallbackRegistry()
    override val listeners = ListenerRegistry()
}