package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.WorldEvent
import lavahack.client.features.subsystem.subsystems.CodeCallerController
import lavahack.client.features.subsystem.subsystems.DevelopmentSettings
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.world.entity.EntityLookup
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * @author _kisman_
 * @since 11:15 of 06.07.2023
 */
@Mixin(ClientWorld::class)
class MixinClientWorld {
    @Shadow
    fun getEntityLookup() : EntityLookup<Entity>? = null

    @Inject(
        method = ["addEntityPrivate"],
        at = [At("HEAD")]
    )
    private fun addEntityPrivateHeadHook(
        id : Int,
        entity : Entity,
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(WorldEvent.Add(entity))
    }

    @Inject(
        method = ["removeEntity"],
        at = [At("HEAD")]
    )
    private fun removeEntityHeadHook(
        id : Int,
        reason : Entity.RemovalReason,
        ci : CallbackInfo
    ) {
        val entity = getEntityLookup()!!.get(id)

        if(entity != null) {
            LavaHack.EVENT_BUS.post(WorldEvent.Remove(entity, reason))
        }
    }

    @Inject(
        method = ["getEntities"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun getEntitiesHeadHook(
        cir : CallbackInfoReturnable<Iterable<Entity>>
    ) {
        if(CodeCallerController.WorldRenderer.FROM_RENDER && DevelopmentSettings.FIX_NULLABLE_ENTITIES.value) {
            val lookup = getEntityLookup()!!
            val nullables = lookup.iterate()
            val notnullables = mutableListOf<Entity>()

            for(entity in nullables) {
                if(entity != null) {
                    notnullables.add(entity)
                }
            }

            cir.returnValue = notnullables
            cir.cancel()
        }
    }
}