package lavahack.client.settings.types

import lavahack.client.features.config.StoredData
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 15:19 of 09.05.2023
 */
class SettingGroup(
    name : String
) : Setting<MutableList<Setting<*>>>(
    name,
    mutableListOf()
) {
    fun <T : Setting<*>>add(
        setting : T
    ) = setting.also {
        register(it)
        it.bound = this
    }

    fun <T : Collection<Setting<*>>> add(
        settings : T
    ) = settings.also {
        for(setting in settings) {
            if(setting.bound == it || setting.bound == null) {
                add(setting)
            }
        }
    }

    override fun save() = StoredData("")

    override fun visible(
        value : () -> Boolean
    ) = super.visible(value) as SettingGroup

    fun link(
        state : Setting<Boolean>
    ) = super.link(
        state,
        SettingGroup("")
    ) { !state.value } as SettingGroup
}