package lavahack.client.features.friend

import lavahack.client.features.config.StoredData
import lavahack.client.utils.client.interfaces.IStorable
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author _kisman_
 * @since 14:24 of 21.05.2023
 */
class Friend(
    var name : String = "",
    var timestamp : Long = -1L
) : IStorable {
    private val index = indexer.getAndAdd(1)

    override fun save() = StoredData(
        "friend.$index",
        "name", name,
        "timestamp", timestamp
    )

    override fun load(
        data : StoredData
    ) {
        name = data.string("name") ?: "NULL"
        timestamp = data.long("timestamp") ?: System.currentTimeMillis()
    }

    companion object {
        var indexer = AtomicInteger(0)
    }
}