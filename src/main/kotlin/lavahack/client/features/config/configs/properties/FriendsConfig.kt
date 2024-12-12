package lavahack.client.features.config.configs.properties

import lavahack.client.features.config.Config
import lavahack.client.features.friend.Friend
import lavahack.client.features.friend.Friends
import lavahack.client.features.module.Module

/**
 * @author _kisman_
 * @since 15:41 of 24.05.2023
 */
@Module.Info(
    name = "Friends",
    messages = false,
    properties = Module.Properties(
        bind = false,
        visible = false
    )
)
object FriendsConfig : Config() {
    override fun save(
        default : Boolean
    ) {
        super.save(default)

        for(friend in Friends.friends) {
            this += friend.save()
        }
    }

    override fun load() {
        super.load()

        Friends.friends.clear()

        for(data in datas.values) {
            val friend = Friend()

            friend.load(data)
            Friends.friends.add(friend)
        }
    }
}