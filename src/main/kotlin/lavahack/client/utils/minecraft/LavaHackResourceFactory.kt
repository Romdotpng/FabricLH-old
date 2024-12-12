package lavahack.client.utils.minecraft

import lavahack.client.LavaHack
import lavahack.client.utils.asStream
import lavahack.client.utils.mc
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceFactory
import net.minecraft.resource.metadata.ResourceMetadata
import net.minecraft.util.Identifier
import java.util.Optional

/**
 * Originally made for ShaderProgram/PostEffectProcessor class
 *
 * @author _kisman_
 * @since 5:09 of 10.06.2023
 */
class LavaHackResourceFactory : ResourceFactory {
    override fun getResource(
        id : Identifier?
    )= Optional.ofNullable<Resource>(null)

    override fun getResourceOrThrow(
        id : Identifier
    ) = try {
        val stream = id.asStream()

        Resource(LavaHack.RESOURCE_PACK, { stream }, ResourceMetadata.NONE_SUPPLIER)
    } catch(_ : Exception) {
        mc.resourceManager.getResourceOrThrow(id)!!
    }
}