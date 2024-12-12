package lavahack.client.mixins

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(PlayerInventory::class)
abstract class MixinPlayerInventory : Inventory {
    @Inject(
        method = ["getStack"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun getStackHeadHook(
        slot : Int,
        cir : CallbackInfoReturnable<ItemStack>
    ) {
        if(slot == -1) {
            cir.returnValue = ItemStack.EMPTY
            cir.cancel()
        }
    }
}