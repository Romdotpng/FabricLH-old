package lavahack.client.utils.chat

import lavahack.client.features.subsystem.subsystems.ColorManager
import lavahack.client.features.subsystem.subsystems.WHITE
import lavahack.client.features.subsystem.subsystems.colored
import lavahack.client.utils.mc
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextColor

/**
 * @author _kisman_
 * @since 10:40 of 08.05.2023
 */
object ChatUtility {
    val ENABLE = Type({ ColorManager.CHAT_ENABLE }, "+")
    val DISABLE = Type({ ColorManager.CHAT_DISABLE }, "-")
    val INFO = Type({ ColorManager.CHAT_INFO }, "!")

    class Type(
        private val color : () -> TextColor,
        private val prefix : String
    ) {
        fun print(
            message : String
        ) {
            if(message.contains("\n")) {
                val split = message.split("\n")

                for(part in split) {
                    print(part)
                }
            } else {
                print(Text.literal(message))
            }
        }

        fun print(
            text : MutableText
        ) {
            if(mc.player != null && mc.world != null) {
                mc.inGameHud.chatHud.addMessage(Text.literal("").append(colored("[LH] ", ColorManager.CHAT_PREFIX)).append(colored("[$prefix] ", color())).append(colored(text, WHITE)))
            }
        }
    }
}