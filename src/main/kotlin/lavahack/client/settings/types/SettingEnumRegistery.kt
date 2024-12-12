package lavahack.client.settings.types

import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.types.combo.Element
import lavahack.client.utils.client.interfaces.ICallbackRegistry
import lavahack.client.utils.client.interfaces.IRegistry
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import java.util.*

/**
 * @author _kisman_
 * @since 9:23 of 26.06.2023
 */
@Suppress("LocalVariableName")
class SettingEnumRegistry<E : Enum<*>>(
    name : String,
    value : E,
    title : String = name,
    onChange : (Setting<Element<E>>) -> Unit = {  }
) : Setting<Element<E>>(
    name,
    Element(value, Arrays.stream(value.javaClass.enumConstants).toList()) { element, setting ->
        val bound = setting?.bound

        if(bound is Module && bound.state) {
            val current = element.current as IRegistry
            val prev = element.prev as IRegistry

            prev.registry.callbacks[1]
            prev.registry.listeners.unsubscribe()
            current.registry.callbacks[0]
            current.registry.listeners.subscribe()
        }
    },
    title,
    onChange
) {
    init {
        this.value.setting = this
    }

    val valEnum : E
        get() = value.current

    override fun onRegister(
        registry : ISettingRegistry
    ) {
        super.onRegister(registry)

        if(registry is ICallbackRegistry && valEnum is IRegistry) {
            for(option in value.list) {
                val _registry = (option as IRegistry).registry
                val group = registry.register(SettingGroup(option.toString()))

                _registry.prefix(option.name)
                registry.register(group.add(_registry))
            }

            registry.enableCallback {
                (valEnum as IRegistry).registry.callbacks[0]
                (valEnum as IRegistry).registry.listeners.subscribe()
            }

            registry.disableCallback {
                (valEnum as IRegistry).registry.callbacks[1]
                (valEnum as IRegistry).registry.listeners.unsubscribe()
            }
        }
    }
}