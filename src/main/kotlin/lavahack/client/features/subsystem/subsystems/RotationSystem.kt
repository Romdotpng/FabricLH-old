package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.PlayerEvent
import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.*
import lavahack.client.utils.client.enums.EntityRotates
import lavahack.client.utils.client.enums.Rotates
import lavahack.client.utils.client.interfaces.impl.*
import net.minecraft.entity.Entity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper

/**
 * @author _kisman_
 * @since 4:17 of 29.07.2023
 */
object RotationSystem : SubSystem(
    "Rotation System"
) {
    val MODE = register(SettingEnum("Mode", Rotates.Packet))
    val ENTITY_ROTATE = register(SettingEnum("Entity Rotate", EntityRotates.BODY))
    private val F5_ROTATIONS = register(Setting("F5 Rotations", true))

    private val rotations = mutableMapOf<Pair<Float, Float>, MutableList<() -> Unit>>()

    //TODO: convert to pairs?
    var requestedYaw = -1f
    var requestedPitch = -1f
    private var lastSentYaw = -1f
    private var lastSentPitch = -1f

    override fun init() {
        var preMotionEvent : PlayerEvent.Motion.Pre? = null
        var lastPreMotionEvent : PlayerEvent.Motion.Pre? = null

        //TODO: create RotationData class or something
        var lastYaw = 0f
        var lastPitch = 0f
        var lastPrevYaw = 0f
        var lastPrevPitch = 0f
        var lastHeadYaw = 0f
        var lastBodyYaw = 0f
        var lastPrevHeadYaw = 0f
        var lastPrevBodyYaw = 0f


        fun handleActions(
            event : PlayerEvent.Motion.Pre?
        ) {
            run {
                for((rotates, actions) in rotations) {
                    var fail1 = false

                    if(MODE.valEnum != Rotates.None) {
                        val yaw = rotates.first
                        val pitch = rotates.second
                        var fail2 = true

                        when(MODE.valEnum) {
                            Rotates.None -> { }
                            Rotates.Packet -> if(event == null) {
                                mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player!!.isOnGround))

                                fail2 = false
                            }
                            Rotates.Normal -> if(event != null) {
                                if(!event.spoofing) {
                                    event.yaw = yaw
                                    event.pitch = pitch
                                }
                            } else {
                                fail1 = true
                                fail2 = false
                            }
                        }

                        if(fail2) {
                            return@run
                        }
                    }

                    for(action in actions) {
                        action()
                    }

                    if(fail1) {
                        return@run
                    }
                }
            }
        }

        motionPreListener {
            handleActions(it)
        }

        motionPreListener(-1) {
            lastPreMotionEvent = preMotionEvent
            preMotionEvent = it
        }

        motionPostListener {
            handleActions(null)

            rotations.clear()
        }

        sendListener(-5) {
            when(
                val packet = it.packet
            ) {
                is PlayerMoveC2SPacket -> {
                    if(packet.changesLook()) {
                        val yaw = packet.getYaw(-1f)
                        val pitch = packet.getPitch(-1f)

                        lastSentYaw = yaw
                        lastSentPitch = pitch
                    }
                }
            }
        }

        listener<Render3DEvent.EntityRenderer.Render.Pre> {
            if(F5_ROTATIONS.value && it.entity == mc.player && preMotionEvent != null) {
                val yaw = MathHelper.wrapDegrees(preMotionEvent!!.yaw)
                val pitch = preMotionEvent!!.pitch
                val prevYaw = MathHelper.wrapDegrees(lastPreMotionEvent?.yaw ?: mc.player!!.yaw)
                val prevPitch = lastPreMotionEvent?.pitch ?: mc.player!!.pitch

                lastYaw = mc.player!!.yaw
                lastPitch = mc.player!!.pitch
                lastPrevYaw = mc.player!!.prevYaw
                lastPrevPitch = mc.player!!.prevPitch
                lastHeadYaw = mc.player!!.headYaw
                lastBodyYaw = mc.player!!.bodyYaw
                lastPrevHeadYaw = mc.player!!.prevHeadYaw
                lastPrevBodyYaw = mc.player!!.prevBodyYaw

                mc.player!!.yaw = yaw
                mc.player!!.pitch = pitch
                mc.player!!.prevYaw = prevYaw
                mc.player!!.prevPitch = prevPitch
                mc.player!!.headYaw = yaw
                mc.player!!.bodyYaw = yaw
                mc.player!!.prevHeadYaw = prevYaw
                mc.player!!.prevBodyYaw = prevYaw
            }
        }

        listener<Render3DEvent.EntityRenderer.Render.Post> {
            if(F5_ROTATIONS.value && it.entity == mc.player && preMotionEvent != null) {
                mc.player!!.yaw = lastYaw
                mc.player!!.pitch = lastPitch
                mc.player!!.prevYaw = lastPrevYaw
                mc.player!!.prevPitch = lastPrevPitch
                mc.player!!.headYaw = lastHeadYaw
                mc.player!!.bodyYaw = lastBodyYaw
                mc.player!!.prevHeadYaw = lastPrevHeadYaw
                mc.player!!.prevBodyYaw = lastPrevBodyYaw
            }
        }
    }

    private fun normalRotate(
        angles : Pair<Float, Float>,
        action : () -> Unit = { }
    ) {
        if(rotations.contains(angles)) {
            rotations[angles]!!.add(action)
        } else {
            rotations[angles] = mutableListOf(action)
        }
    }

    //TODO: wtf?
    fun rotate(
        angles : Pair<Float, Float>,
        rotate : Rotates = MODE.valEnum,
        action : () -> Unit = { }
    ) {
        when(rotate) {
            Rotates.None -> action()
            Rotates.Packet -> packetRotate(angles, action)
            Rotates.Normal -> normalRotate(angles, action)
        }
    }

    fun requiresRotationPacket() = (requestedYaw != lastSentYaw || requestedPitch != lastSentPitch) && requestedYaw != -1f && requestedPitch != -1f
}

fun rotate(
    angles : Pair<Number, Number>,
    rotate : Rotates = RotationSystem.MODE.valEnum,
    action : () -> Unit = { }
) {
    RotationSystem.rotate(angles.first.toFloat() to angles.second.toFloat(), rotate, action)
}

fun rotate(
    pos : BlockPos,
    rotate : Rotates = RotationSystem.MODE.valEnum,
    action : () -> Unit = { }
) {
    rotate(pos.rotates, rotate, action)
}

fun rotate(
    entity : Entity,
    rotate : Rotates = RotationSystem.MODE.valEnum,
    action : () -> Unit = { }
) {
    rotate(entity.rotates, rotate, action)
}