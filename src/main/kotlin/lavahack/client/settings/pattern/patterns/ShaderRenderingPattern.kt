package lavahack.client.settings.pattern.patterns

import lavahack.client.settings.pattern.Pattern
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.register

@Suppress("PrivatePropertyName")
class ShaderRenderingPattern : Pattern() {
    private val CURRENT_SHADER = register(SettingEnum("Shader", CoreShaders.None))

    init {
        for(shader in CoreShaders.values()) {
            val group = register(SettingGroup(shader.screenShader.displayName))

            register(group.add(shader.screenShader))
        }
    }

    fun start() {
        CURRENT_SHADER.valEnum.screenShader.begin()
    }

    fun end() {
        CURRENT_SHADER.valEnum.screenShader.end()
    }
}