package lavahack.client.features.command.args

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import lavahack.client.utils.mc
import net.minecraft.command.CommandSource

class PlayerArgumentType : ArgumentType<String> {
    override fun parse(
        reader : StringReader
    ) = reader.readString()!!

    override fun <S : Any> listSuggestions(
        context : CommandContext<S>,
        builder : SuggestionsBuilder
    ) = if(mc.networkHandler != null) {
        val stream = mc.networkHandler!!.playerList.stream().map { it.profile.name }

        CommandSource.suggestMatching(stream, builder)!!
    } else {
        super.listSuggestions(context, builder)!!
    }

    companion object {
        fun player() = PlayerArgumentType()

        fun player(
            context : CommandContext<*>,
            name : String
        ) = context.getArgument(name, String::class.java)
    }
}