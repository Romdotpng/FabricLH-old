package lavahack.client.mixins

import com.google.gson.JsonElement
import net.minecraft.client.gl.GlUniform
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.util.InvalidHierarchicalFileException
import net.minecraft.util.JsonHelper
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import java.util.*

@Mixin(ShaderProgram::class)
class MixinShaderProgram {
    /*@Inject(
        method = ["addUniform"],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun addUniformHeadHook(
        json : JsonElement
    ) {
        val jobject = JsonHelper.asObject(json, "uniform")
        val name = JsonHelper.getString(jobject, "name")
        val type = GlUniform.getTypeIndex(JsonHelper.getString(jobject, "type"))
        val count = JsonHelper.getInt(jobject, "count")
        val values = JsonHelper.getArray(jobject, "values")
        val stack = FloatArray(count)

        if (values.size() != count && values.size() > 1) {
            throw InvalidHierarchicalFileException("Invalid amount of values specified (expected " + count + ", found " + values.size() + ")")
        } else {
            var k = 0

            for((i, value) in values.withIndex()) {
                stack[i] = if(values.isEmpty) {
                    0f
                } else {
                    val jelement = value as JsonElement

                    JsonHelper.asFloat(jelement, "value")
                }


            }

            val var9 : Iterator<*> = values.iterator()
            while (var9.hasNext()) {
                val jsonElement = var9.next() as JsonElement
                try {
                    stack[k] = JsonHelper.asFloat(jsonElement, "value")
                } catch (var13 : Exception) {
                    val invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(var13)
                    invalidHierarchicalFileException.addInvalidKey("values[$k]")
                    throw invalidHierarchicalFileException
                }
                ++k
            }
            if (count > 1 && values.size() == 1) {
                while (k < count) {
                    stack[k] = stack[0]
                    ++k
                }
            }
            val l = if (count > 1 && count <= 4 && type < 8) count - 1 else 0
            val glUniform = GlUniform(name, type + l, count, this)
            if (type <= 3) {
                glUniform.setForDataType(stack[0].toInt(), stack[1].toInt(), stack[2].toInt(), stack[3].toInt())
            } else if (type <= 7) {
                glUniform.setForDataType(stack[0], stack[1], stack[2], stack[3])
            } else {
                glUniform.set(Arrays.copyOfRange(stack, 0, count))
            }
            this.uniforms.add(glUniform)
        }
    }*/
}