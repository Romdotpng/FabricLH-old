package lavahack.client.features.module.modules.movement

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.InputController
import lavahack.client.mixins.AccessorLivingEntity
import lavahack.client.settings.Setting
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.client.option.KeyBinding

/**
 * @author _kisman_
 * @since 1:04 of 29.05.2023
 */
@Module.Info(
    name = "NoSlow",
    description = "Keeps your movement while eating or etc.",
    category = Module.Category.MOVEMENT
)
object NoSlow : Module() {
    val ITEMS = register(Setting("Items", false))

    init {
        val jump = register(Setting("Jump", false))
        val guiMove = register(Setting("Gui Move", true))

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            if(jump.value) {
                (mc.player as AccessorLivingEntity).jumpingCooldown = 0
            }

            if(guiMove.value && mc.currentScreen != null) {
                mc.player!!.input.movementForward = 0f
                mc.player!!.input.movementSideways = 0f

                fun processKey(
                    binding : KeyBinding,
                    block1 : () -> Unit,
                    block2 : (Boolean) -> Unit
                ) {
                    val key = binding.defaultKey
                    val code = key.code

                    KeyBinding.setKeyPressed(key, InputController.pressedKey(code))

                    if (InputController.pressedKey(code)) {
                        block1()
                        block2(true)
                    } else {
                        block2(false)
                    }
                }

                processKey(mc.options!!.forwardKey, { ++mc.player!!.input!!.movementForward }, { mc.player!!.input!!.pressingForward })
                processKey(mc.options!!.backKey, { --mc.player!!.input!!.movementForward }, { mc.player!!.input!!.pressingBack })
                processKey(mc.options!!.leftKey, { ++mc.player!!.input!!.movementSideways }, { mc.player!!.input!!.pressingLeft })
                processKey(mc.options!!.rightKey, { --mc.player!!.input!!.movementSideways }, { mc.player!!.input!!.pressingRight })
            }
        }
    }
}