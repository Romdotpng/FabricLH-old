package lavahack.client.mixins

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import net.minecraft.world.entity.EntityIndex
import net.minecraft.world.entity.EntityLike
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(EntityIndex::class)
class MixinEntityIndex {
    @Redirect(
        method = ["add"],
        at = At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectFunction;put(ILnet/minecraft/world/entity;)V"
        )
    )
    private fun addPutInvokeHook(
        instance : Int2ObjectFunction<EntityLike>,
        key : Int,
        value : EntityLike
    ) {
        try {
            instance.put(key, value)
        } catch(exception : ArrayIndexOutOfBoundsException) {
            println("$key $value")

            throw exception
        }
    }
}