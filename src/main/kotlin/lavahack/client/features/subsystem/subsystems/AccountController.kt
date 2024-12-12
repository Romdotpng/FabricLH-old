package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem

/**
 * TODO: backend implementation
 *
 * @author _kisman_
 * @since 12:04 of 25.05.2023
 */
object AccountController : SubSystem(
    "Account Controller"
) {
    val DATA = AccountData.byKey("NULL")
}

class AccountData(
    val name : String,
    val rank : Int,
    val key : String
) {
    companion object {
        fun byKey(
            key : String
        ) : AccountData {
            val name = "NULL"
            val rank = -1

            return AccountData(name, rank, key)
        }
    }
}