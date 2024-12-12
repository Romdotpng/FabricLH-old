package lavahack.client.features.command.commands

import com.mojang.brigadier.arguments.StringArgumentType
import lavahack.client.features.command.Command
import lavahack.client.features.command.args.ModuleArgumentType
import lavahack.client.features.command.argument
import lavahack.client.features.command.literal
import net.minecraft.command.argument.RegistryKeyArgumentType
import org.lwjgl.glfw.GLFW

@Command.Info(
    names = ["bind"],
    description = "Binds a specified module"
)
class BindCommand : Command() {
    init {
        /*executeCallback = { builder ->
            builder.then(
                argument("module", ModuleArgumentType())
                    .then(
                        argument("key", StringArgumentType.string())
                            .executes { context ->
                                val module = ModuleArgumentType.module(context, "module")
                                val key = StringArgumentType.getString(context, "key")

                                GLFW.key()

                                1
                            }
                    )
            )
        }*/
    }
}