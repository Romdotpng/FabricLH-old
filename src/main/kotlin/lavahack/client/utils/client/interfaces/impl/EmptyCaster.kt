package lavahack.client.utils.client.interfaces.impl

import lavahack.client.utils.client.interfaces.ICaster

class EmptyCaster : ICaster<Nothing> {
    override fun cast(
        setting : Any
    ) = throw IllegalStateException()
}