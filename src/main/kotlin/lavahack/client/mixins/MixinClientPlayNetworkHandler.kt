package lavahack.client.mixins

import com.mojang.brigadier.exceptions.CommandSyntaxException
import lavahack.client.LavaHack
import lavahack.client.utils.chat.ChatUtility
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientPlayNetworkHandler::class)
class MixinClientPlayNetworkHandler {
    @Inject(
        method = ["sendChatMessage"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun sendChatMessageHeadHook(
        content : String,
        ci : CallbackInfo
    ) {
        if(content.startsWith(LavaHack.PREFIX) && content.removePrefix(LavaHack.PREFIX).isNotBlank()) {
            try {
                LavaHack.COMMAND_DISPATCHER.execute(LavaHack.COMMAND_DISPATCHER.parse(content.removePrefix(LavaHack.PREFIX), LavaHack.COMMAND_SOURCE))
            } catch(exception : CommandSyntaxException) {
                val context = exception.context

                ChatUtility.INFO.print("Command syntax exception: $context")
            }

            ci.cancel()
        }
    }
}