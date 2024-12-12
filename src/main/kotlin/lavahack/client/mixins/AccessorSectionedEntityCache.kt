package lavahack.client.mixins

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.LongSortedSet
import net.minecraft.world.entity.EntityLike
import net.minecraft.world.entity.EntityTrackingSection
import net.minecraft.world.entity.SectionedEntityCache
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 23:42 of 10.07.2023
 */
@Mixin(SectionedEntityCache::class)
interface AccessorSectionedEntityCache {
    @get:Accessor("trackedPositions")
    val trackedPositions : LongSortedSet

    @Accessor("trackingSections")
    fun <T : EntityLike> trackingSections() : Long2ObjectMap<EntityTrackingSection<T>>
}