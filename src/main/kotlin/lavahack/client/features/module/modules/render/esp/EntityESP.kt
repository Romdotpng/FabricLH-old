package lavahack.client.features.module.modules.render.esp

import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BoxRenderingPattern
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.enums.Entities
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.type

@Suppress("PrivatePropertyName")
@Module.Info(
    name = "EntityESP",
    display = "Entities",
    description = "Highlights entities around you",
    submodule = true
)
class EntityESP : Module() {
    private val MODE_SETTING_CASTER = SettingEnum.Caster<Modes>()

    init {
        val patterns = mutableMapOf<Entities, BoxRenderingPattern>()

        val settings = registerEntitiesSettings { it0, it1 ->
            val group = SettingGroup("Box")
            val pattern = group.add(BoxRenderingPattern(tracer = true, arrow = true))

            group.prefix("Render")

            patterns[it0] = pattern

            it1[MODE_SETTING_CASTER] = SettingEnum("Mode", Modes.None)
            it1[EmptyCaster()] = group
        }

        listener<Render3DEvent.RenderEntity.Post> {
            val entity = it.entity
            val type = entity.type()

            if(entity != mc.player && type != null) {
                val mode = settings.get(entity, MODE_SETTING_CASTER)

                when(mode.valEnum) {
                    Modes.None -> { }
                    Modes.Box -> {
                        val pattern = patterns[type]!!
                        val box = entity.boundingBox

                        pattern.draw(it.matrices, box)
                    }
                }
            }
        }
    }

    enum class Modes {
        None,
        Box
    }
}