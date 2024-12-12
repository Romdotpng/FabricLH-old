package lavahack.client.features.command

import lavahack.client.LavaHack
import lavahack.client.features.command.commands.FriendCommand

object Commands {
    private val commands = mutableListOf<Command>()

    fun init() {
        LavaHack.LOGGER.info("Initializing commands")

        add(FriendCommand())
    }

    private fun add(
        command : Command
    ) {
        commands.add(command)

        val builder = literal(command.info.names[0])

        command.execute(builder)

        LavaHack.COMMAND_DISPATCHER.register(builder)
    }
}