package lavahack.client.mixins

import lavahack.client.features.module.modules.render.ViewModel
import lavahack.client.utils.mc
import lavahack.client.utils.rotate
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.item.HeldItemRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.PickaxeItem
import net.minecraft.item.SwordItem
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * @author _kisman_
 * @since 14:31 of 11.07.2023
 */
@Mixin(HeldItemRenderer::class)
class MixinHeldItemRenderer {
    @Inject(
        method = ["renderFirstPersonItem"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
            shift = At.Shift.AFTER
        )]
    )
    private fun renderFirstPersonItemPushInvokeHook(
        player : AbstractClientPlayerEntity,
        tickDelta : Float,
        pitch : Float,
        hand : Hand,
        swingProgress : Float,
        item : ItemStack,
        equipProgress : Float,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        if(ViewModel.state) {
            val arm = if (hand == Hand.MAIN_HAND) player.mainArm else player.mainArm.opposite!!
            val settings = if(arm == Arm.LEFT) ViewModel.LEFT_HAND else ViewModel.RIGHT_HAND
            /*val stack = player.mainHandStack
            val item = stack.item

            if(arm == Arm.RIGHT && ViewModel.CUSTOM_SWING_STATE.value && mc.options.useKey.isPressed && ((item is SwordItem && ViewModel.CUSTOM_SWING_SWORD.value) || (item is PickaxeItem && ViewModel.CUSTOM_SWING_PICKAXE.value))) {

            }*/
            matrices.scale(settings.SCALE_X.value, settings.SCALE_Y.value, settings.SCALE_Z.value)
            matrices.translate(settings.TRANSLATE_X.value, settings.TRANSLATE_Y.value, settings.TRANSLATE_Z.value)
            matrices.rotate(settings.ROTATE_X.value, settings.ROTATE_Y.value, settings.ROTATE_Z.value)
        }
    }

    @Inject(
        method = ["renderItem"],
        at = [At("HEAD")]
    )
    private fun renderItemHeadHook(
        entity : LivingEntity,
        stack : ItemStack,
        renderMode : ModelTransformationMode,
        leftHanded : Boolean,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        matrices.push()

        if(entity is ClientPlayerEntity && ViewModel.state) {
            val settings = if(leftHanded) ViewModel.LEFT_ITEM else ViewModel.RIGHT_ITEM

            matrices.scale(settings.SCALE_X.value, settings.SCALE_Y.value, settings.SCALE_Z.value)
            matrices.translate(settings.TRANSLATE_X.value, settings.TRANSLATE_Y.value, settings.TRANSLATE_Z.value)
            matrices.rotate(settings.ROTATE_X.value, settings.ROTATE_Y.value, settings.ROTATE_Z.value)
        }
    }

    @Inject(
        method = ["renderItem"],
        at = [At("TAIL")]
    )
    private fun renderItemTailHook(
        entity : LivingEntity,
        stack : ItemStack,
        renderMode : ModelTransformationMode,
        leftHanded : Boolean,
        matrices : MatrixStack,
        vertexConsumers : VertexConsumerProvider,
        light : Int,
        ci : CallbackInfo
    ) {
        matrices.pop()
    }
}