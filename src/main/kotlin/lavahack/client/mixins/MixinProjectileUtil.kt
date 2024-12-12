package lavahack.client.mixins

import lavahack.client.features.module.modules.player.NoEntityTrace
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.util.function.Predicate

/**
 * @author _kisman_
 * @since 15:48 of 15.07.2023
 */
@Mixin(ProjectileUtil::class)
class MixinProjectileUtil {
    private companion object {
        @JvmStatic
        @Inject(
            method = ["raycast"],
            at = [At("HEAD")],
            cancellable = true
        )
        private fun raycastHeadHook(
            entity : Entity,
            min : Vec3d,
            max : Vec3d,
            box : Box,
            predicate : Predicate<Entity>,
            d : Double,
            cir : CallbackInfoReturnable<EntityHitResult>
        ) {
            if(NoEntityTrace.state) {
                cir.returnValue = null
                cir.cancel()
            }
        }
    }
}