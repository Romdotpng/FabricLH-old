package lavahack.client.features.module.modules.render

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.client.interfaces.impl.worldListener
import lavahack.client.utils.render.world.line
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.entity.projectile.thrown.EnderPearlEntity
import net.minecraft.util.math.Vec3d
import java.awt.Color

@Module.Info(
    name = "Trails",
    description = "Shows trajectories of entities",
    category = Module.Category.RENDER
)
class Trails : Module() {
    init {
        val color1 = register(Setting("Color1", Colour(-1)))
        val color2 = register(Setting("Color2", Colour(-1)))
        val width = register(SettingNumber("Width", 1f, 0.1f..5f))
        val shader = register(SettingEnum("Shader", CoreShaders.None))

        //TODO: rewrite via registerEntitiesSettings(idk if i need it)
        val self = register(Setting("Self", true))
        val players = register(Setting("Players", false))
        val pearls = register(Setting("Pearls", false))
        val arrows = register(Setting("Arrows", false))

        val fadeGroup = register(SettingGroup("Fade"))
        val fadeState = register(fadeGroup.add(Setting("State", false)))
        val fadeLength = register(SettingNumber("Length", 1000L, 1L..1000L))
        val fadeEasing = register(SettingEnum("Easing", Easings.Linear))

        fadeGroup.prefix("Fade")

        val trails = mutableMapOf<Entity, MutableMap<Vec3d, Stopwatch>>()

        enableCallback {
            trails.clear()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                trails.clear()

                return@tickListener
            }

            for(entity in mc.world!!.entities) {
                if((entity == mc.player && self.value) || (entity is PlayerEntity && players.value) || (entity is EnderPearlEntity && pearls.value) || (entity is ArrowEntity && arrows.value)) {
                    if(trails.contains(entity)) {
                        trails[entity]!![entity.pos] = Stopwatch()
                    } else {
                        trails[entity] = mutableMapOf(entity.pos to Stopwatch())
                    }
                }
            }
        }

        worldListener {
            shader.beginWorld()

            for ((_, datas) in trails.entries) {
                val points = mutableMapOf<Vec3d, Color>()
                val length = datas.size

                for ((index, entry) in datas.toMutableMap().entries.withIndex()) {
                    val vec = entry.key
                    val stopwatch = entry.value
                    val delta = if(fadeState.value) System.currentTimeMillis() - stopwatch.timestamp else fadeLength.value
                    val percent = delta.toDouble() / fadeLength.value.toDouble()
                    //TODO: rewrite using div().mul()
                    val color = color2.value.veci.copy().sub(color1.value.veci).div(length).mul(index).add(color1.value.veci).color()

                    color.alpha((fadeEasing.valEnum.dec(percent) * color.alpha.toDouble()).toInt())

                    if(fadeState.value && stopwatch.passed(fadeLength.value)) {
                        datas.remove(vec)
                    }

                    points[vec] = color
                }

                line(
                    it.matrices,
                    points,
                    width.value
                )
            }

            shader.endWorld()
        }
    }
}