package lavahack.client.mixins

import lavahack.client.LavaHack
import lavahack.client.event.events.ScreenEvent
import lavahack.client.event.events.TickEvent
import lavahack.client.event.events.WindowEvent
import lavahack.client.features.config.Configs
import lavahack.client.features.module.modules.exploit.MultiTask
import lavahack.client.features.module.modules.render.ShadersModule
import lavahack.client.features.subsystem.subsystems.BlurController
import lavahack.client.utils.mc
import lavahack.client.utils.render.shader.POSTPROCESS_SHADERS
import net.minecraft.client.MinecraftClient
import net.minecraft.client.RunArgs
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.GameOptions
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * @author _kisman_
 * @since 13:27 of 08.05.2023
 */
@Suppress("FunctionName")
@Mixin(MinecraftClient::class)
class MixinMinecraftClient {
    @Shadow
    val options : GameOptions? = null

    @Shadow
    val player : ClientPlayerEntity? = null

    @Shadow
    private fun doAttack() = true

    @Inject(
        method = ["tick"],
        at = [At("HEAD")]
    )
    private fun tickHeadHook(
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(TickEvent.Pre())
    }

    @Inject(
        method = ["tick"],
        at = [At("HEAD")]
    )
    private fun tickTailHook(
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(TickEvent.Post())
    }

    @Inject(
        method = ["<init>"],
        at = [At("TAIL")]
    )
    private fun __init__TailHook(
        args : RunArgs,
        ci : CallbackInfo
    ) {
        LavaHack.LOGGER.info("Creating postprocess shaders")

        for(shader in POSTPROCESS_SHADERS) {
            shader.create()
        }

        BlurController.SHADER.create()
    }

    @Inject(
        method = ["setScreen"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun setScreenHeadHook(
        screen : Screen?,
        ci : CallbackInfo
    ) {
        if(ScreenEvent.Open.STATE) {
            LavaHack.EVENT_BUS.post(ScreenEvent.Open(screen), ci)
        }
    }

    @Inject(
        method = ["onResolutionChanged"],
        at = [At("TAIL")]
    )
    private fun onResolutionChangedTailHook(
        ci : CallbackInfo
    ) {
        LavaHack.EVENT_BUS.post(WindowEvent.Resize())
    }

    @Inject(
        method = ["handleInputEvents"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z",
            shift = At.Shift.BEFORE,
            ordinal = 2
        )]
    )
    private fun handleInputEventsIsPressedInvokeHook(
        ci : CallbackInfo
    ) {
        if(MultiTask.state) {
            while(options!!.attackKey.wasPressed()) {
                doAttack()
            }
        }
    }

    @Inject(
        method = ["doItemUse"],
        at = [At("HEAD")]
    )
    private fun doItemUseHeadHook(
        ci : CallbackInfo
    ) {
        if(MultiTask.state) {
            MultiTask.BREAKING_BLOCK = (mc.interactionManager as AccessorClientPlayerInteractionManager).breakingBlock
            (mc.interactionManager as AccessorClientPlayerInteractionManager).breakingBlock = false
        }
    }

    @Inject(
        method = ["doItemUse"],
        at = [At("TAIL")]
    )
    private fun doItemUseTailHook(
        ci : CallbackInfo
    ) {
        if(MultiTask.state && !(mc.interactionManager as AccessorClientPlayerInteractionManager).breakingBlock) {
            (mc.interactionManager as AccessorClientPlayerInteractionManager).breakingBlock = MultiTask.BREAKING_BLOCK
        }
    }

    @Inject(
        method = ["handleBlockBreaking"],
        at = [At("HEAD")]
    )
    private fun handleBlockBreakingHeadHook(
        breaking : Boolean,
        ci : CallbackInfo
    ) {
        if(MultiTask.state) {
            MultiTask.HAND_ACTIVE = player!!.isUsingItem
            (player as AccessorClientPlayerEntity).usingItem = false
        }
    }

    @Inject(
        method = ["handleBlockBreaking"],
        at = [At("TAIL")]
    )
    private fun handleBlockBreakingTailHook(
        breaking : Boolean,
        ci : CallbackInfo
    ) {
        if(MultiTask.state && !player!!.isUsingItem) {
            (player as AccessorClientPlayerEntity).usingItem = MultiTask.HAND_ACTIVE
        }
    }

    @Inject(
        method = ["hasOutline"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun hasOutlineHeadHook(
        entity : Entity,
        cir : CallbackInfoReturnable<Boolean>
    ) {
        if(ShadersModule.state && (ShadersModule.PLAYERS.value && entity is PlayerEntity)) {
            cir.returnValue = true
            cir.cancel()
        }
    }
}