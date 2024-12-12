package lavahack.client.mixins

import lavahack.client.features.subsystem.subsystems.DevelopmentSettings
import net.minecraft.client.gl.JsonEffectShaderProgram
import org.slf4j.Logger
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(JsonEffectShaderProgram::class)
class MixinJsonEffectShaderProgram {
    @Redirect(
        method = ["finalizeUniformsAndSamplers"],
        at = At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
        )
    )
    private fun finalizeUniformsAndSamplerRedirectLoggerWarnHook(
        logger : Logger,
        string : String,
        value1 : Any,
        value2 : Any
    ) {
        if(DevelopmentSettings.SHADER_WARNS.value) {
            logger.warn(string, value1, value2)
        }
    }
}