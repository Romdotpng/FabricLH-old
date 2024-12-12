package lavahack.client.features.command.args

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import lavahack.client.features.module.Module
import lavahack.client.features.module.Modules
import net.minecraft.text.Text

class ModuleArgumentType : ArgumentType<Module> {
    override fun parse(
        reader : StringReader
    ) = Modules.names[reader.readString()] ?: throw DynamicCommandExceptionType { Text.of("Module $it does not exist!") }.create(reader.readString())

    companion object {
        fun module() = ModuleArgumentType()

        fun module(
            context : CommandContext<*>,
            name : String
        ) = context.getArgument(name, java.lang.Module::class.java)!!
    }
}