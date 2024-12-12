package lavahack.client.features.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.command.CommandSource

fun <T> argument(
    name : String,
    type : ArgumentType<T>
) = RequiredArgumentBuilder.argument<CommandSource, T>(name, type)!!

fun literal(
    name : String
) = LiteralArgumentBuilder.literal<CommandSource>(name)!!