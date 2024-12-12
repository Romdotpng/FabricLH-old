package lavahack.client.features.module.modules.render

import com.mojang.blaze3d.systems.RenderSystem
import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.Colour
import lavahack.client.utils.client.interfaces.impl.get
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.registerEntitiesSettings
import lavahack.client.utils.type
import java.awt.Color

/**
 * @author Cubic
 */
@Module.Info(
    name = "Chams",
    description = ":)",
    category = Module.Category.WIP
)
class Chams : Module() {
    private val WIRE_STATE_CASTER = Setting.Caster<Boolean>()
    private val MODEL_STATE_CASTER = Setting.Caster<Boolean>()

    private val WIRE_COLOR_CASTER = Setting.Caster<Color>()
    private val MODEL_COLOR_CASTER = Setting.Caster<Color>()

    init {
        val settings = registerEntitiesSettings { it0, it1 ->
            it1[WIRE_STATE_CASTER] = Setting("Wire", false)
            it1[MODEL_STATE_CASTER] = Setting("Model", false)

            val wireGroup = SettingGroup("Wire")
            val wireColorGroup = wireGroup.add(SettingGroup("Colors"))
//            val wireCrowdAlphaGroup = wireGroup.add(SettingGroup("Crowd"))

            val modelGroup = SettingGroup("Model")
            val modelColorGroup = wireGroup.add(SettingGroup("Colors"))
//            val modelCrowdAlphaGroup = wireGroup.add(SettingGroup("Crowd"))

            it1[WIRE_COLOR_CASTER] = wireColorGroup.add(Setting("Color", Colour(-1)))
            it1[MODEL_COLOR_CASTER] = modelColorGroup.add(Setting("Model", Colour(-1)))

            for(flag in Flags.values()) {
                val wireCaster = flag.WIRE_CASTER
                val modelCaster = flag.MODEL_CASTER

                val wireSetting = wireGroup.add(Setting(flag.name, false))
                val modelSetting = modelGroup.add(Setting(flag.name, false))

                it1[wireCaster] = wireSetting
                it1[modelCaster] = modelSetting
            }

            wireGroup.prefix("Wire")
            wireColorGroup.prefix("Wire")

            modelGroup.prefix("Model")
            modelColorGroup.prefix("Model")
        }


        listener<Render3DEvent.EntityRenderer.RenderModel> {
            val entity = it.entity
            val type = entity.type()

            if(type != null) {
                val wireState = settings.get(entity, WIRE_STATE_CASTER)
                val modeleState = settings.get(entity, MODEL_STATE_CASTER)


            }
        }
    }

    enum class Flags(
        val beginIfTrue : () -> Unit,
        val beginIfFalse : () -> Unit,
        val afterIfTrue : () -> Unit,
        val afterIfFalse : () -> Unit
    ) {
        Depth(
            { RenderSystem.enableDepthTest() },
            { RenderSystem.disableDepthTest() },
            { RenderSystem.disableDepthTest() },
            { RenderSystem.enableDepthTest() }
        ),
        Culling(
            { RenderSystem.enableCull() },
            { RenderSystem.disableCull() },
            { RenderSystem.disableCull() },
            { RenderSystem.enableCull() }
        ),
        Blend(
            { RenderSystem.enableBlend() },
            { RenderSystem.disableBlend() },
            { RenderSystem.disableBlend() },
            { RenderSystem.enableBlend() }
        ),
        /*Translucent(
//            { RenderSystem.blendFunc() }
            { }, { }, { }, { }
        ),
        Texture2D(
            { GlStateManager.texture }
        )*/

        ;

        val WIRE_CASTER = Setting.Caster<Boolean>()
        val MODEL_CASTER = Setting.Caster<Boolean>()
    }
}