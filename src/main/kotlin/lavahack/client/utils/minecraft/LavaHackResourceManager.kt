package lavahack.client.utils.minecraft

import lavahack.client.LavaHack
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourcePack
import net.minecraft.util.Identifier
import java.util.function.Predicate

/**
 * @author _kisman_
 * @since 13:06 of 10.08.2023
 */
class LavaHackResourceManager : ResourceManager {
    override fun getResource(
        id : Identifier?
    ) = LavaHack.RESOURCE_FACTORY.getResource(id)

    override fun getResourceOrThrow(
        id : Identifier
    ) = LavaHack.RESOURCE_FACTORY.getResourceOrThrow(id)

    override fun getAllNamespaces() = setOf<String>()

    override fun getAllResources(
        id : Identifier
    ) = listOf<Resource>()

    override fun findResources(
        startingPath : String,
        allowedPathPredicate : Predicate<Identifier>
    ) = mapOf<Identifier, Resource>()

    override fun findAllResources(
        startingPath : String,
        allowedPathPredicate : Predicate<Identifier>
    ) = mapOf<Identifier, List<Resource>>()

    override fun streamResourcePacks() = listOf<ResourcePack>().stream()
}