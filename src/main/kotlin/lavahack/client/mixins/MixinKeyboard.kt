package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.InputEvent
import lavahack.client.features.subsystem.subsystems.InputController
import lavahack.client.utils.keyAction
import net.minecraft.client.Keyboard
import org.lwjgl.glfw.GLFW
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 13:36 of 08.05.2023
 */
@Mixin(Keyboard::class)
class MixinKeyboard {
    @Inject(
        method = ["onKey"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun onKeyHeadHook(
        window : Long,
        key : Int,
        scancode : Int,
        action : Int,
        modifiers : Int,
        ci : CallbackInfo
    ) {
        if(key != -1) {
            InputController.keyState(key, action != GLFW.GLFW_RELEASE)

            if(InputController.can()) {
                LavaHack.EVENT_BUS.post(InputEvent.Keyboard(key, modifiers, keyAction(action)), ci)
            }
        }
    }
}