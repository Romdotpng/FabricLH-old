package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.enums.CoreShaders
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.render.shader.CORE_SHADERS

object CoreShadersController : SubSystem(
    "Core Shaders Controller",
    true
) {
    override fun preinit() {
        for(shader in CORE_SHADERS) {
            shader.parse()
        }

        val screenGroup = register(SettingGroup("Screen"))
        val worldGroup = register(SettingGroup("World"))

        for(shader in CoreShaders.values()) {
            val screenShaderGroup = register(screenGroup.add(SettingGroup(shader.screenShader.displayName)))
            val worldShaderGroup = register(worldGroup.add(SettingGroup(shader.worldShader.displayName)))

            register(screenShaderGroup.add(shader.screenShader))
            register(worldShaderGroup.add(shader.worldShader))
        }

        screenGroup.prefix("Screen")
        worldGroup.prefix("World")
    }
}