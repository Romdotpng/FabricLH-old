package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.InputEvent
import lavahack.client.features.module.modules.misc.FreeLook
import lavahack.client.features.subsystem.subsystems.InputController
import lavahack.client.utils.keyAction
import net.minecraft.client.Mouse
import net.minecraft.client.network.ClientPlayerEntity
import org.lwjgl.glfw.GLFW
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.abs

/**
 * @author _kisman_
 * @since 13:43 of 08.05.2023
 */
@Mixin(Mouse::class)
class MixinMouse {
    @Inject(
        method = ["onMouseButton"],
        at = [At("HEAD")],
        cancellable = true
    )
    fun onMouseButtonHeadHook(
        window : Long,
        button : Int,
        action : Int,
        mods : Int,
        ci : CallbackInfo
    ) {
        InputController.buttonState(button, action != GLFW.GLFW_RELEASE)

        if(InputController.can()) {
            LavaHack.EVENT_BUS.post(InputEvent.Mouse(button, mods, keyAction(action)), ci)
        }
    }

    @Redirect(
        method = ["updateMouse"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
        )
    )
    fun updateMouseChangeLookDirection(
        player : ClientPlayerEntity,
        deltaX : Double,
        deltaY : Double
    ) {
        if(FreeLook.state) {
            FreeLook.yaw +=  deltaX.toFloat() / FreeLook.SENSITIVITY.value
            FreeLook.pitch += deltaY.toFloat() / FreeLook.SENSITIVITY.value

            if(abs(FreeLook.pitch) > 90.0F) {
                FreeLook.pitch = if(FreeLook.pitch > 0.0F) 90.0F else -90.0F
            }
        } else {
            player.changeLookDirection(deltaX, deltaY)
        }
    }
}