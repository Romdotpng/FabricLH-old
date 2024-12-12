package lavahack.client.mixins

import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import lavahack.client.LavaHack
import net.minecraft.client.gui.screen.ChatInputSuggestor
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.command.CommandSource
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.concurrent.CompletableFuture

@Mixin(ChatInputSuggestor::class)
class MixinChatInputSuggestor {
    @Shadow
    private var parse : ParseResults<CommandSource>? = null

    @Shadow
    @Final
    private var textField : TextFieldWidget? = null

    @JvmField
    @Shadow
    val completingSuggestions = false

    @Shadow
    private var pendingSuggestions : CompletableFuture<Suggestions>? = null

    @Shadow
    private var window : ChatInputSuggestor.SuggestionWindow? = null

    @Shadow
    fun show(
        narrateFirstSuggestion : Boolean
    ) {

    }

    @Inject(
        method = ["refresh"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun refreshHeadHook(
        ci : CallbackInfo
    ) {
        //Illegal classload request for lavahack.client.mixins.MixinChatInputSuggestor. Mixin is defined in lavahack.mixins.json and cannot be referenced directly
        /*val string = textField!!.text
        val reader = StringReader(string)

        if(reader.canRead(LavaHack.PREFIX.length) && reader.string.startsWith(LavaHack.PREFIX, reader.cursor)) {
            reader.cursor += LavaHack.PREFIX.length

            parse = LavaHack.COMMAND_DISPATCHER.parse(reader, LavaHack.COMMAND_SOURCE)

            if(textField!!.cursor >= 1 && (window == null || !completingSuggestions)) {
                pendingSuggestions = LavaHack.COMMAND_DISPATCHER.getCompletionSuggestions(parse, textField!!.cursor)
                pendingSuggestions!!.thenRun {
                    if(pendingSuggestions!!.isDone) {
                        show(false)
                    }
                }
            }

            ci.cancel()
        }*/
    }

    /*@Inject(
        method = ["refresh"],
        at = [At("HEAD"
//            value = "INVOKE",
//            target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
//            remap = false
        )],
        cancellable = true,
//        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private fun refreshCanRenderInvokeHook(
        ci : CallbackInfo,
//        string : String,
//        reader : StringReader
    ) {
        val string = textField!!.text
        val reader = StringReader(string)

        if(reader.canRead(LavaHack.PREFIX.length) && reader.string.startsWith(LavaHack.PREFIX, reader.cursor)) {
            reader.cursor += LavaHack.PREFIX.length

            parse = LavaHack.COMMAND_DISPATCHER.parse(reader, LavaHack.COMMAND_SOURCE)

            if(textField!!.cursor >= 1 && (window == null || !completingSuggestions)) {
                pendingSuggestions = LavaHack.COMMAND_DISPATCHER.getCompletionSuggestions(parse, textField!!.cursor)
                pendingSuggestions!!.thenRun {
                    if(pendingSuggestions!!.isDone) {
                        show(false)
                    }
                }
            }

            ci.cancel()
        }
    }*/

}