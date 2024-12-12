package lavahack.client.features.config.configs.features

import lavahack.client.features.config.Config
import lavahack.client.features.gui.huds.HudEditor
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.interfaces.impl.register

@Module.Info(
    name = "Hud Editor",
    messages = false,
    properties = Module.Properties(
        bind = false,
        visible = false
    )
)
object HudEditorConfig : Config() {
    private val states = mutableMapOf<Setting<*>, () -> Boolean>()

    init {
        val selectAll = register(Setting("Select All", false) {
            for(setting in states.keys) {
                states[setting] = { it.value }
            }
        })

        for(setting in HudEditor) {
            if(setting !is SettingGroup) {
                val state = Setting(setting.name, false).visible { !selectAll.value }

                states[setting] = state
                register(state)
            }
        }
    }

    override fun save(
        default : Boolean
    ) {
        super.save(default)

        for(entry in states) {
            val setting = entry.key
            val state = entry.value()

            if(state || default) {
                this += setting.save()
            }
        }
    }

    override fun load() {
        super.load()

        for(entry in datas) {
            val data = entry.value
            val prefix = entry.key
            val split = prefix.split(".")
            val name = split[2]

            for(setting in states.keys) {
                if(name == setting.name) {
                    setting.load(data)
                }
            }
        }
    }
}