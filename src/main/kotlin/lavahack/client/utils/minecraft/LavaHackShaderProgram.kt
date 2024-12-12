package lavahack.client.utils.minecraft

import lavahack.client.LavaHack
import lavahack.client.utils.render.shader.Shader
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.render.VertexFormat

class LavaHackShaderProgram(
    val shader : Shader,
    name : String,
    format : VertexFormat
) : ShaderProgram(
    LavaHack.RESOURCE_FACTORY,
    name,
    format
)