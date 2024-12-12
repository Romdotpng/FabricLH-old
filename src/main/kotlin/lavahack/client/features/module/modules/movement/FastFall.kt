package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.Stopwatch
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.moving
import lavahack.client.utils.velocityY
import lavahack.client.utils.world.trace
import lavahack.client.utils.world.traceDown
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.Direction
import kotlin.math.max

/**
 * @author cattyn/oldfashion
 */
@Module.Info(
    name = "FastFall",
    description = "Faster falling.",
    aliases = "ReverseStep",
    category = Module.Category.MOVEMENT
)
class FastFall : Module() {
    init {
        val mode = register(SettingEnum("Mode", Modes.Normal))
        val speed = register(SettingNumber("Speed", 10.0, 1.0..15.0))
        val height = register(SettingNumber("Height", 10, 1..30))
        val onlySnap = register(Setting("Only Snap", false))
        val holeCheck = register(Setting("Hole Check", false))
//        val phaseCheck = register(Setting("Phase Check", true))

        val lagbackGroup = register(SettingGroup("Lagback"))
        val lagbackState = register(lagbackGroup.add(Setting("State", false)))
        val lagbackTimeout = register(lagbackGroup.add(SettingNumber("Timeout", 1000L, 1L..1500L)))
        val lagbackAction = register(lagbackGroup.add(SettingEnum("Action", LagbackActions.Disable)))
        val lagbackModifiedSpeed = register(lagbackGroup.add(SettingNumber("Modified Speed", 10.0, 10.0..15.0)))

        val developmentGroup = register(SettingGroup("Development"))
        val checkIfWas = register(developmentGroup.add(Setting("Was On Ground", false)))
        val legacyShoota = register(developmentGroup.add(Setting("Legacy Shoota", false)))
        val oneBlockShoota = register(developmentGroup.add(Setting("One Block Shoota", false)))
        val switchOnStrafe = register(developmentGroup.add(Setting("Xenophobia", false)))
        val strafeMode = register(developmentGroup.add(SettingEnum("Xenophobia Mode", Modes.Fast)))

        lagbackGroup.prefix("Lagback")

        val lagbackTimer = Stopwatch()

        var falling = false
        var wasOnGround = false

        fun lagging() = lagbackState.value && lagbackTimer.passed(lagbackTimeout.value)

        fun speed() = if(lagging() && lagbackAction.valEnum == LagbackActions.ModifySpeed) {
            lagbackModifiedSpeed.value
        } else {
            speed.value
        }

        fun mode() : Modes {
            if(lagging()) {
                when(lagbackAction.valEnum) {
                    LagbackActions.ToNormal -> return Modes.Normal
                    LagbackActions.ToFast -> return Modes.Fast
                    else -> { }
                }
            }

            return if(Speed.state && switchOnStrafe.value) {
                strafeMode.valEnum
            } else {
                mode.valEnum
            }
        }

        fun handleMode(
            mode : Modes
        ) {
            when(mode) {
                Modes.Shoota -> {
                    if(legacyShoota.value && mc.player!!.isOnGround && mc.player!!.velocity.y <= 0) {
                        val range = mc.player!!.boundingBox.expand(0.0, -height.value.toDouble(), 0.0).contract(0.0, mc.player!!.height.toDouble(), 0.0)
                        val collisions = mc.world!!.getBlockCollisions(null, range)

                        if(collisions.toList().isNotEmpty()) {
                            var y = 0.0

                            for(collision in collisions) {
                                val maxY = collision.getMax(Direction.Axis.Y)

                                y = max(y, maxY)
                            }

                            mc.player!!.updatePosition(mc.player!!.x, y, mc.player!!.z)
                        }
                    }
                }

                else -> {
                    if(mc.player!!.isOnGround && !onlySnap.value) {
                        mc.player!!.velocityY -= speed()
                    }
                }
            }
        }

        enableCallback {
            falling = false
        }

        tickListener {
            //TODO: liquid check
            //TODO: phase check
            if(mc.player == null || mc.world == null || !moving() || (lagging() && lagbackAction.valEnum == LagbackActions.Disable)) {
                return@tickListener
            }

            val result = traceDown()
            val blocks = result.blocks
            val hole = result.hole

            if(blocks <= height.value && (!holeCheck.value || hole)) {
                if(blocks == 1 && oneBlockShoota.value && !(Speed.state && switchOnStrafe.value)) {
                    handleMode(Modes.Shoota)
                } else {
                    handleMode(mode.valEnum)
                }
            }
        }

        moveListener {
            falling = false
            //TODO: liquid check
            //TODO: phase check

            val result = traceDown()
            val blocks = result.blocks
            val hole = result.hole

            if(blocks <= height.value && (!holeCheck.value || hole) && trace()) {
                var flag = false

                if((mode.valEnum == Modes.Shoota || (blocks == 1 && oneBlockShoota.value)) && !legacyShoota.value && !(Speed.state && switchOnStrafe.value)) {
                    if(checkIfWas.value) {
                        if(!mc.player!!.isOnGround && wasOnGround) {
                            mc.player!!.updatePosition(mc.player!!.x, mc.player!!.y - blocks, mc.player!!.z)
                            flag = true
                        }
                    } else {
                        if(mc.player!!.isOnGround && mc.player!!.velocity.y <= 0) {
                            mc.player!!.updatePosition(mc.player!!.x, mc.player!!.y - blocks, mc.player!!.z)
                            flag = true
                        }
                    }
                }

                if(mode.valEnum == Modes.Fast && mc.player!!.isOnGround && !flag) {
                    it.x = it.x.toDouble() * 0.05
                    it.z = it.z.toDouble() * 0.05

                    falling = true
                }

                wasOnGround = mc.player!!.isOnGround
            }
        }

        receiveListener {
            when(it.packet) {
                is PlayerPositionLookS2CPacket -> {
                    if(lagbackState.value) {
                        lagbackTimer.reset()
                    }
                }
            }
        }
    }

    enum class Modes {
        Normal,
        Fast,
        Shoota
    }

    enum class LagbackActions {
        Disable,
        ToNormal,
        ToFast,
        ModifySpeed
    }
}