package lavahack.client.utils.client.interfaces

import lavahack.client.features.config.StoredData

/**
 * @author _kisman_
 * @since 13:33 of 21.05.2023
 */
interface IStorable {
    fun save() : StoredData

    fun load(
        data : StoredData
    )
}