package lavahack.client.features.config.configs.features

import lavahack.client.features.config.Config
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 11:49 of 28.05.2023
 */
abstract class AbstractModulesConfig(
    private val modules : List<Module>,
    private val names : Map<String, Module>
) : Config() {
    val states = mutableMapOf<Module, () -> Boolean>()

    init {
        val selectAll = register(Setting("Select All", false) {
            for(module in states.keys) {
                states[module] = { it.value }
            }
        })

        for(module in modules) {
            val state = Setting(module.info.name, false).visible { !selectAll.value }

            states[module] = state
            register(state)
        }
    }

    override fun save(
        default : Boolean
    ) {
        super.save(default)

        for(entry in states) {
            val module = entry.key
            val state = entry.value()

            if(state || default) {
                this += module.save()

                for(setting in module.registry.settings) {
                    if(setting.savable) {
                        if (setting.prefix == null) {
                            setting.prefix = module
                        }

                        this += setting.save()
                    }
                }
            }
        }
    }

    override fun load() {
        super.load()

        for(entry in datas) {
            val data = entry.value
            val prefix = entry.key
            val split = prefix.split(".")
            val nameOfModule = split[1]

            if(names.contains(nameOfModule)) {
                val module = names[nameOfModule]!!

                if(split.size == 2) {
                    module.load(data)
                } else if(split.size > 3) {
                    val nameOfSetting = split[3]

                    if(module.registry.names.contains(nameOfSetting)) {
                        val setting = module.registry.names[nameOfSetting]!!

                        setting.load(data)
                    }
                }
            }
        }
    }
}