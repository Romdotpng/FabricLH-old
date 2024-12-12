package lavahack.client.utils.minecraft

import net.minecraft.resource.InputSupplier
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourceType
import net.minecraft.resource.metadata.ResourceMetadataReader
import net.minecraft.util.Identifier
import java.io.InputStream

/**
 * @author _kisman_
 * @since 5:13 of 10.06.2023
 */
class LavaHackResourcePack : ResourcePack {
    override fun getName() = "LavaHack"

    override fun close() {

    }

    override fun openRoot(
        vararg segments : String?
    ) : InputSupplier<InputStream>? = null

    override fun open(
        type : ResourceType?,
        id : Identifier?
    ) : InputSupplier<InputStream>? = null

    override fun findResources(
        type : ResourceType?,
        namespace : String?,
        prefix : String?,
        consumer : ResourcePack.ResultConsumer?
    ) {

    }

    override fun getNamespaces(
        type : ResourceType?
    ) : MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> parseMetadata(
        metaReader : ResourceMetadataReader<T>?
    ) : T? {
        TODO("Not yet implemented")
    }
}