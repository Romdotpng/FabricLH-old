package lavahack.client.mixins

import com.mojang.authlib.GameProfile
import lavahack.client.LavaHack
import lavahack.client.event.events.PacketEvent
import lavahack.client.event.events.PlayerEvent
import lavahack.client.features.module.modules.movement.NoSlow
import lavahack.client.features.subsystem.subsystems.RotationSystem
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.MovementType
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * @author _kisman_
 * @since 1:12 of 29.05.2023
 */
@Mixin(ClientPlayerEntity::class)
class MixinClientPlayerEntity(
    world : ClientWorld,
    profile : GameProfile
) : AbstractClientPlayerEntity(
    world,
    profile
) {
    private var callingFromTickMovement = false
    private var lastYaw0 = 0f
    private var lastPitch0 = 0f

    @Shadow
    fun isCamera() = true

    @JvmField
    var lastOnGround = true

    @Inject(
        method = ["tickMovement"],
        at = [At("HEAD")]
    )
    private fun tickMovementHeadHook(
        ci : CallbackInfo
    ) {
        callingFromTickMovement = true
    }

    @Inject(
        method = ["tickMovement"],
        at = [At("TAIL")]
    )
    private fun tickMovementTailHook(
        ci : CallbackInfo
    ) {
        callingFromTickMovement = false
    }

    @Inject(
        method = ["isUsingItem"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun isUsingItemHeadHook(
        cir : CallbackInfoReturnable<Boolean>
    ) {
        if (callingFromTickMovement && NoSlow.state && NoSlow.ITEMS.value) {
            cir.returnValue = false
            cir.cancel()
        }
    }

    @Inject(
        method = ["move"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun moveHeadHook(
        type : MovementType,
        movement : Vec3d,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(PlayerEvent.Move(type, movement.x, movement.y, movement.z), ci) {
            super.move(it.type, Vec3d(it.x.toDouble(), it.y.toDouble(), it.z.toDouble()))
        }
    }

    @Inject(
        method = ["sendMovementPackets"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun sendMovementPacketsHeadHook(
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(PacketEvent.Send.Movement.Pre(), ci)
    }

    @Inject(
        method = ["sendMovementPackets"],
        at = [At("TAIL")],
        cancellable = true
    )
    private fun sendMovementPacketsTailHook(
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(PacketEvent.Send.Movement.Post())
    }

    @Inject(
        method = ["tick"],
        at = [At("HEAD")]
    )
    private fun tickHeadHook(
        ci : CallbackInfo
    ) {
//        lastPos = pos
        lastYaw0 = yaw
        lastPitch0 = pitch

        val event = PlayerEvent.Motion.Pre()

        PlayerEvent.Motion.IS_IN = true
        PlayerEvent.Motion.Pre.IS_IN = true

        LavaHack.EVENT_BUS.post(event)

        PlayerEvent.Motion.Pre.IS_IN = false

        //TODO: different spoofing fields for movement and rotation
        if(event.spoofing) {
//            setPosition(x, y, z)
            yaw = event.yaw
            pitch = event.pitch
        }
    }

    @Inject(
        method = ["tick"],
        at = [At("TAIL")]
    )
    private fun tickTailHook(
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(PlayerEvent.Motion.Post())

        //TODO: rewrite it
        /*if(pos != lastPos) {
            setPosition(lastPos)
        }*/

        yaw = lastYaw0
        pitch = lastPitch0

        PlayerEvent.Motion.IS_IN = false
    }

    @Redirect(
        method = ["tick"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
        )
    )
    private fun tickRedirectClientPlayNetworkHandlerSendPacketHook(
        networkHandler : ClientPlayNetworkHandler,
        packet : Packet<*>
    ) {
        if(packet is PlayerMoveC2SPacket && !packet.changesLook() && RotationSystem.requiresRotationPacket()) {
            val yaw = RotationSystem.requestedYaw
            val pitch = RotationSystem.requestedPitch
            val ground = packet.isOnGround

            if(packet.changesPosition()) {
                val x = packet.getX(0.0)
                val y = packet.getX(0.0)
                val z = packet.getX(0.0)

                networkHandler.sendPacket(PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, ground))
            } else {
                networkHandler.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, ground))
            }
        } else {
            networkHandler.sendPacket(packet)

            if(packet is ClientCommandC2SPacket && isCamera()) {
                lastOnGround = !isOnGround
            }
        }
    }
}