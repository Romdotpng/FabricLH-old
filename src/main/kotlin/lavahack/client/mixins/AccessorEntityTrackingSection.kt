package lavahack.client.mixins

import net.minecraft.util.collection.TypeFilterableList
import net.minecraft.world.entity.EntityTrackingSection
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 23:44 of 10.07.2023
 */
@Mixin(EntityTrackingSection::class)
interface AccessorEntityTrackingSection {
    @Accessor("collection")
    fun <T> collection() : TypeFilterableList<T>
}