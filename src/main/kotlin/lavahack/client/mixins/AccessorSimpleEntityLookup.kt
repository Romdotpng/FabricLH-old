package lavahack.client.mixins

import net.minecraft.world.entity.EntityIndex
import net.minecraft.world.entity.EntityLike
import net.minecraft.world.entity.SectionedEntityCache
import net.minecraft.world.entity.SimpleEntityLookup
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 23:41 of 10.07.2023
 */
@Mixin(SimpleEntityLookup::class)
interface AccessorSimpleEntityLookup {
    @Accessor("index")
    fun <T : EntityLike> index() : EntityIndex<T>

    @Accessor("cache")
    fun <T : EntityLike> cache() : SectionedEntityCache<T>
}