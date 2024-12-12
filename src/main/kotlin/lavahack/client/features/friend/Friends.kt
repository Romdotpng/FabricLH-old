package lavahack.client.features.friend

/**
 * @author _kisman_
 * @since 14:24 of 21.05.2023
 */
object Friends {
    val friends = mutableListOf<Friend>()
    val names = HashSet<String>()

    fun init() {

    }

    operator fun plusAssign(
        name : String
    ) {
        friends.add(Friend(name, System.currentTimeMillis()))
        names.add(name.lowercase())
    }

    operator fun plusAssign(
        friend : Friend
    ) {
        friends.add(friend)
        names.add(friend.name.lowercase())
    }

    operator fun minusAssign(
        name : String
    ) {
        for(friend in friends.toList()) {
            if(friend.name.lowercase() == name.lowercase()) {
                friends.remove(friend)
            }
        }

        names.remove(name.lowercase())
    }
}