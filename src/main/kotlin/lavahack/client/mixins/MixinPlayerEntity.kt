package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.PlayerEvent
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("USELESS_IS_CHECK")
@Mixin(PlayerEntity::class)
abstract class MixinPlayerEntity : PlayerEntity(
    null,
    null,
    0f,
    null
) {
    @Inject(
        method = ["jump"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun jumpHeadHook(
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(PlayerEvent.Jump(this is ClientPlayerEntity), ci)
    }
}