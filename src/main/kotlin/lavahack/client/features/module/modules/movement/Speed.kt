package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.features.module.modules.movement.speed.InstantSpeed
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnumRegistry
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.IRegistry
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.math.sqrt
import net.minecraft.entity.MovementType
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import kotlin.math.max
import kotlin.math.min

/**
 * @author _kisman_
 * @since 11:58 of 25.06.2023
 */
@Module.Info(
    name = "Speed",
    description = "Faster movement.",
    aliases = "Strafe",
    category = Module.Category.MOVEMENT,
    modules = [
        InstantSpeed::class
    ]
)
object Speed : Module() {
    init {
        val mode = register(SettingEnumRegistry("Mode", Modes.Strafe1))

        displayInfo = { mode.value.current.toString() }
    }

    enum class Modes(
        override val registry : Registry
    ) : IRegistry {
        Strafe1(object : Registry() {
            init {
                val potionMultiplier = register(SettingNumber("Potion Multiplier", 1.0, 0.0..1.0))

                moveListener {
                    if(it.type != MovementType.SELF) {
                        return@moveListener
                    }

                    if(moving()) {
                        val motions = strafe(potionMultiplier.value)

                        if (mc.player!!.isOnGround) {
                            mc.player!!.jump()

                            it.y = mc.player!!.velocity.y
                        }

                        it.x = motions[0]
                        it.z = motions[1]
                    } else {
                        it.x = 0.0
                        it.z = 0.0
                    }

                    it.cancel()
                }
            }
        }),
        Strafe2(object : Registry() {
            init {
                val speed = register(SettingNumber("Speed", SPRINTING_SPEED, 0.05..1.0))
                val potionMultiplier = register(SettingNumber("Potion Multiplier", 1.0, 0.0..1.0))
//                val strict = register(Setting("Strict", false))
//                val sprint = register(Setting("Sprint", true))
                val boost = register(Setting("Boost", false))

                var currentSpeed = 0.0
                var prevMotion = 0.0
                var maxVelocity = 0.0
                var oddStage = false
                var stage = 4

                val velocityTimer = Stopwatch()

                var sneaking = true

                enableCallback {
                    currentSpeed = moveSpeed(potionMultiplier.value)
                    prevMotion = 0.0
                    maxVelocity = 0.0
                    stage = 4
                }

                tickListener {
                    if(mc.player == null || mc.world == null) {
                        return@tickListener
                    }

                    if(!(mc.options.forwardKey.isPressed || mc.options.backKey.isPressed || mc.options.leftKey.isPressed || mc.options.rightKey.isPressed)) {
                        currentSpeed = 0.0
                    }

                    val diffX = mc.player!!.x - mc.player!!.prevX
                    val diffZ = mc.player!!.z - mc.player!!.prevZ

                    prevMotion = sqrt(diffX * diffX + diffZ * diffZ).toDouble()

//                    if(sprint.value && !mc.player!!.isSprinting)

                    //strict
                }

                moveListener {
                    if(stage != 1 || (mc.player!!.forwardSpeed == 0f || mc.player!!.sidewaysSpeed == 0f)) {
                        if(stage == 2 && (mc.player!!.forwardSpeed != 0f || mc.player!!.sidewaysSpeed != 0f)) {
                            val jumpSpeed = if(mc.player!!.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
                                (mc.player!!.getStatusEffect(StatusEffects.JUMP_BOOST)!!.amplifier + 1) * 0.1
                            } else {
                                0.0
                            }

                            mc.player!!.velocityY = 0.3999 + jumpSpeed
                            it.y = 0.3999 + jumpSpeed

                            currentSpeed *= if(oddStage) {
                                1.6835
                            } else {
                                1.395
                            }
                        } else if(stage == 3) {
                            val adjustedMotion = 0.66 * (prevMotion - moveSpeed(speed.value, potionMultiplier.value))

                            currentSpeed = prevMotion - adjustedMotion
                            oddStage = !oddStage
                        } else {
                            val collisionBoxes = mc.world!!.getCollisions(mc.player, mc.player!!.boundingBox.offset(0.0, mc.player!!.velocity.y, 0.0)).toList()

                            if((collisionBoxes.isNotEmpty() || mc.player!!.verticalCollision) && stage > 0) {
                                stage = if(mc.player!!.forwardSpeed == 0f && mc.player!!.sidewaysSpeed == 0f) {
                                    0
                                } else {
                                    1
                                }
                            }

                            currentSpeed = prevMotion - prevMotion / 159.0
                        }
                    } else {
                        currentSpeed = 1.35 * moveSpeed(speed.value, potionMultiplier.value) - 0.01
                    }

                    currentSpeed = max(currentSpeed, moveSpeed(speed.value, potionMultiplier.value))

                    currentSpeed = if(maxVelocity > 0 && boost.value && !velocityTimer.passed(75L, true) && !mc.player!!.horizontalCollision) {
                        max(currentSpeed, maxVelocity)
                    } else {
                        min(currentSpeed, 0.443)
                    }

                    if(mc.player!!.isOnGround) {
                        currentSpeed = moveSpeed(speed.value, potionMultiplier.value)
                    }

                    val motions = if(mc.player!!.forwardSpeed == 0f && mc.player!!.sidewaysSpeed == 0f) {
                        arrayOf(0.0, 0.0)
                    } else {
                        strafe(speed = currentSpeed)
                    }

                    it.x = motions[0]
                    it.z = motions[1]

                    if(!(mc.player!!.forwardSpeed == 0f && mc.player!!.sidewaysSpeed == 0f)) {
                        stage++
                    }

                }

                receiveListener {
                    when(it.packet) {
                        is PlayerPositionLookS2CPacket -> {
                            currentSpeed = 0.0
                            prevMotion = 0.0
                            maxVelocity = 0.0
                        }
                        is ExplosionS2CPacket -> {
                            val velocityX = it.packet.playerVelocityX
                            val velocityZ = it.packet.playerVelocityZ

                            maxVelocity = sqrt(velocityX * velocityX + velocityZ * velocityZ).toDouble()
                            velocityTimer.reset()
                        }
                    }
                }
            }
        }),
        YPort(object : Registry() {
            init {
                val speed = register(SettingNumber("Speed", 1.0, 0.0..2.0))

                moveListener {
                    if(it.type != MovementType.SELF) {
                        return@moveListener
                    }

                    if(moving()) {
                        if(mc.player!!.isOnGround) {
                            val motions = strafe(speed = moveSpeed(SPRINTING_SPEED * speed.value, 1.0))

                            it.x = motions[0]
                            it.z = motions[1]
                            it.cancel()
                        }
                    }
                }
            }
        })
    }
}