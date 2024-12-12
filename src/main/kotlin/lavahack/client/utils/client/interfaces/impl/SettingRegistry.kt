package lavahack.client.utils.client.interfaces.impl

import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.enums.Entities
import lavahack.client.utils.client.interfaces.ICaster
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.type
import net.minecraft.entity.Entity

/**
 * @author _kisman_
 * @since 12:59 of 15.05.2023
 */

class SettingRegistry(
    registry : ISettingRegistry? = null,
    default : List<Setting<*>> = emptyList()
) {
    val names = mutableMapOf<String, Setting<*>>()
    val settings = mutableListOf<Setting<*>>()

    init {
        for(setting in default) {
            setting.onRegister(registry!!)

            add(setting)
        }
    }

    fun add(
        setting : Setting<*>
    ) {
        settings.add(setting)

        if(setting !is SettingGroup) {
            names[setting.name] = setting
        }
    }

    fun addAll(
        settings : Collection<Setting<*>>
    ) {
        for(setting in settings) {
            add(setting)
        }
    }
}

fun <T : Setting<*>> ISettingRegistry.register(
    setting : T
) = setting.also {
    setting.onRegister(this)

    registry.add(it)
}

fun <T : Collection<Setting<*>>> ISettingRegistry.register(
    settings : T
) = settings.also {
    for(setting in settings) {
        if(setting.bound == it) {
            setting.bound = this
        }

        register(setting)
    }
}

fun (Collection<Setting<*>>).prefix(
    prefix : String
) {
    fun updateName(
        setting : Setting<*>,
        bound : Collection<Setting<*>>?
    ) {
        if(bound != null) {
            if(bound is ISettingRegistry) {
                bound.registry.names[setting.name] = setting
            }

            if(bound is Setting<*>) {
                updateName(setting, bound.bound)
            }
        }
    }

    for(setting in this) {
        setting.name = "$prefix ${setting.name}"

        if(setting !is SettingGroup) {
            updateName(setting, this)
        }

        setting.prefix(prefix)
    }
}

fun ISettingRegistry.registerEntitiesSettings(
    entitySettings : (Entities, MutableMap<ICaster<*>, Setting<*>>) -> Unit
) : MutableMap<Entities, MutableMap<ICaster<*>, Setting<*>>> {
    val settings = mutableMapOf<Entities, MutableMap<ICaster<*>, Setting<*>>>()

    for(entity in Entities.values()) {
        val group = register(SettingGroup(entity.name))
        val settingsOfEntity = mutableMapOf<ICaster<*>, Setting<*>>()

        entitySettings(entity, settingsOfEntity)
        register(group.add(settingsOfEntity.values))
        group.prefix(entity.name)

        settings[entity] = settingsOfEntity
    }

    return settings
}

fun <T : Any> (MutableMap<Entities, MutableMap<ICaster<*>, Setting<*>>>).get(
    entity : Entity,
    caster : ICaster<T>
) : T {
    val type = entity.type()
    val settings = this[type]!!
    val setting = settings[caster]!!

    return caster.cast(setting)
}