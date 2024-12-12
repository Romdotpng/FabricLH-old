package lavahack.client.mixins

import net.minecraft.client.MinecraftClient
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import java.util.*

/**
 * @author _kisman_
 * @since 10:58 of 24.05.2023
 */
@Mixin(MinecraftClient::class)
interface AccessorMinecraftClient {
    @Accessor("renderTaskQueue") fun renderTaskQueue() : Queue<Runnable>?

    @get:Accessor("itemUseCooldown")
    @set:Accessor("itemUseCooldown")
    var itemUseCooldown : Int
}