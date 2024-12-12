package lavahack.client.mixins

import net.minecraft.entity.Entity
import net.minecraft.world.World
import net.minecraft.world.entity.EntityLookup
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

/**
 * @author _kisman_
 * @since 23:37 of 10.07.2023
 */
@Mixin(World::class)
interface InvokerWorld {
    @get:Invoker("getEntityLookup")
    val entityLookup : EntityLookup<Entity>
}