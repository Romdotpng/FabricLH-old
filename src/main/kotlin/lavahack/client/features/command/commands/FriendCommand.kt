package lavahack.client.features.command.commands

import lavahack.client.features.command.Command
import lavahack.client.features.command.args.PlayerArgumentType
import lavahack.client.features.command.argument
import lavahack.client.features.command.literal
import lavahack.client.features.friend.Friends
import lavahack.client.utils.chat.ChatUtility

@Command.Info(
    names = ["friend", "friends", "fans"],
    description = "Manages friend list"
)
class FriendCommand : Command() {
    init {
        executeCallback = { builder ->
            builder.then(
                literal("add")
                    .then(
                        argument("player", PlayerArgumentType.player())
                            .executes { context ->
                                val player = PlayerArgumentType.player(context, "player")

                                Friends += player

                                ChatUtility.INFO.print("Friended $player!")

                                1
                            }
                    )
            ).then(
                literal("remove")
                    .then(
                        argument("player", PlayerArgumentType.player())
                            .executes { context ->
                                val player = PlayerArgumentType.player(context, "player")

                                Friends -= player

                                ChatUtility.INFO.print("Unfriended $player!")

                                1
                            }
                    )
            ).then(
                literal("list")
                    .executes { _ ->
                        ChatUtility.INFO.print("Friends: ${Friends.names.joinToString(", ")}")

                        1
                    }
            ).executes {
                ChatUtility.INFO.print("Usages:\nfriend add/remove <player>\nfriend list")

                1
            }
        }
    }
}