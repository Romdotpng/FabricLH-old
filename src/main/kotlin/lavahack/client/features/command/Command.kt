package lavahack.client.features.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource

open class Command {
    val info = javaClass.getAnnotation(Info::class.java)!!

    protected var executeCallback : (LiteralArgumentBuilder<CommandSource>) -> Unit = { }

    fun execute(
        builder : LiteralArgumentBuilder<CommandSource>
    ) {
        executeCallback(builder)
    }

    annotation class Info(
        val names : Array<String>,
        val description : String = ""
    )
}