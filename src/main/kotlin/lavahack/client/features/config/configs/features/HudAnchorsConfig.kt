package lavahack.client.features.config.configs.features

import lavahack.client.features.config.Config
import lavahack.client.features.config.StoredData
import lavahack.client.features.hud.Hud
import lavahack.client.features.hud.Huds
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.utils.client.enums.HudAnchors
import lavahack.client.utils.client.interfaces.impl.register
import java.util.Enumeration

@Module.Info(
    name = "Hud Anchors",
    messages = false,
    properties = Module.Properties(
        bind = false,
        visible = false
    )
)
object HudAnchorsConfig : Config() {
    private val states = mutableMapOf<Setting<*>, () -> Boolean>()
    private val anchors = mutableMapOf<Setting<*>, HudAnchors>()

    init {
        val selectAll = register(Setting("Select All", false) {
            for(setting in states.keys) {
                states[setting] = { it.value }
            }
        })

        for(anchor in HudAnchors.values()) {
            val setting = Setting(anchor.name, false).visible { !selectAll.value }

            states[setting] = setting
            anchors[setting] = anchor
            register(setting)
        }
    }

    override fun save(
        default : Boolean
    ) {
        super.save(default)

        for(entry in states) {
            val setting = entry.key
            val state = entry.value()
            val anchor = anchors[setting]!!

            if(state || default) {
                this += StoredData(
                    "hudahchor.${setting.name}",
                    anchor.draggables.joinToString(",") { it.toString() }
                )
            }
        }
    }

    override fun load() {
        for((prefix, data) in datas) {
            try {
                val anchor = HudAnchors.valueOf(prefix.split(".")[1])
                val names = data.entries[data.entries.keys.first()]!!.split(",")

                for(name in names) {
                    val hitbox = Huds.huds.find { it.name == name }?.HITBOX

                    if(hitbox != null) {
                        anchor.draggables.add(hitbox)
                    }
                }
            } catch(_ : IllegalArgumentException) { }
        }
    }
}