package lavahack.client.features.module.modules.render.esp

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Colour
import lavahack.client.utils.Stopwatch
import lavahack.client.utils.beginWorld
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.endWorld
import lavahack.client.utils.render.world.horizontalCircle
import net.minecraft.entity.EntityType
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min

@Module.Info(
    name = "SpawnsESP",
    display = "Spawns",
    submodule = true
)
class SpawnsESP : Module() {
    init {
        val color = register(Setting("Color", Colour(-1)))
        val radius = register(SettingNumber("Radius", 0.5, 0.1..2.0))
        val width = register(SettingNumber("Width", 1f, 0.1f..5f))
        val lengthPriority = register(SettingEnum("Length Priority", LengthPriorities.Global))
        val globalLength = register(SettingNumber("Global Length", 1000L, 100L..5000L, "Length").visible { lengthPriority.valEnum == LengthPriorities.Global })
        val delay = register(SettingNumber("Delay", 0L, 0L..1000L))
        val shader = register(SettingEnum("Shader", CoreShaders.None))

        val entities = register(SettingGroup("Entities"))
        val crystals = register(entities.add(Setting("Crystals", true)))
        val boats = register(entities.add(Setting("Boats", false)))

        val extensionGroup = register(SettingGroup("Extension"))
        val extensionState = register(extensionGroup.add(Setting("State", false)))
        val extensionEasing = register(extensionGroup.add(SettingEnum("Easing", Easings.Linear)))
        val extensionLength = register(extensionGroup.add(SettingNumber("Length", 1000L, 100L..3000L).visible { lengthPriority.valEnum != LengthPriorities.Global }))
        val extensionRadius = register(extensionGroup.add(SettingNumber("Radius", 2.0, -5.0..5.0, "Max Radius %")))

        //TODO: better name
        val risingGroup = register(SettingGroup("Rising"))
        val risingState = register(risingGroup.add(Setting("State", true)))
        val risingEasing = register(risingGroup.add(SettingEnum("Easing", Easings.Linear)))
        val risingLength = register(risingGroup.add(SettingNumber("Length", 1000L, 100L..5000L).visible { lengthPriority.valEnum != LengthPriorities.Global }))
        val risingHeight = register(risingGroup.add(SettingNumber("Height", 1.0, -2.0..2.0, "Max Height %")))


        extensionGroup.prefix("Extension")
        risingGroup.prefix("Rising")

        val stopwatch = Stopwatch()

        val spawns = mutableMapOf<Long, Vec3d>()

        fun reset() {
            spawns.clear()
        }

        enableCallback {
            reset()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                reset()
            }
        }

        receiveListener {
            when(
                val packet = it.packet
            ) {
                is EntitySpawnS2CPacket -> {
                    if(stopwatch.passed(delay.value, true)) {
                        val type = packet.entityType

                        if((crystals.value && type == EntityType.END_CRYSTAL) || (boats.value && (type == EntityType.BOAT || type == EntityType.CHEST_BOAT))) {
                            val centre = Vec3d(packet.x, packet.y, packet.z)
                            val timestamp = System.currentTimeMillis()

                            spawns[timestamp] = centre
                        }
                    }
                }
            }
        }

        worldListener {
            shader.beginWorld()

            for((timestamp, centre) in spawns.toMutableMap()) {
                val firstLength = if(extensionState.value) extensionLength.value else 0L
                val secondLength = if(risingState.value) risingLength.value else 0L
                val delta = (System.currentTimeMillis() - timestamp).toDouble()

                val maxLength = when(lengthPriority.valEnum) {
                    LengthPriorities.Min -> min(firstLength, secondLength)
                    LengthPriorities.Max -> max(firstLength, secondLength)
                    LengthPriorities.Global -> globalLength.value
                }

                if(delta > maxLength) {
                    spawns.remove(timestamp)

                    continue
                }

                val height = if(risingState.value) {
                    val length = (if(lengthPriority.valEnum == LengthPriorities.Global) globalLength.value else risingLength.value).toDouble()
                    val percent = risingEasing.valEnum.function(delta / length)

                    percent * risingHeight.value
                } else {
                    0.0
                }

                val extendedRadius = if(extensionState.value) {
                    val length = (if(lengthPriority.valEnum == LengthPriorities.Global) globalLength.value else extensionLength.value).toDouble()
                    val percent = extensionEasing.valEnum.function(delta / length)

                    radius.value + percent * extensionRadius.value
                } else {
                    radius.value
                }

                val pos = centre.add(0.0, height, 0.0)

                horizontalCircle(
                    it.matrices,
                    pos,
                    color.value,
                    width.value,
                    extendedRadius
                )
            }

            shader.endWorld()
        }
    }

    enum class LengthPriorities {
        Min,
        Max,
        Global
    }
}