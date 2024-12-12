package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.module.modules.render.ShadersModule
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.enums.PostprocessShaders
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.worldListener
import lavahack.client.utils.render.shader.POSTPROCESS_SHADERS
import lavahack.client.utils.render.shader.PostProcessShader

/**
 * @author _kisman_
 * @since 12:37 of 04.06.2023
 */
object PostprocessShaderRenderer : SubSystem(
    "Postprocess Shader Renderer",
    true
) {
    val CURRENT_SHADER = register(SettingEnum("Shader", PostprocessShaders.DEFAULT))

    val tasks = mapOf<Int, MutableList<() -> Unit>>(
        1 to mutableListOf(),
        0 to mutableListOf(),
        -1 to mutableListOf(),
        -2 to mutableListOf()
    )

    override fun preinit() {
        for(shader in POSTPROCESS_SHADERS) {
            shader.parse()
        }

        for(shader in PostprocessShaders.values()) {
            val group = register(SettingGroup(shader.shader.displayName))

            register(group.add(shader.shader))
        }
    }

    override fun init() {
        fun processPriority(
            priority : Int
        ) {
            for(task in tasks[priority] ?: emptyList()) {
                task()
            }
        }

        fun empty() : Boolean {
            for((priority, task) in tasks.entries) {
                if(priority != -2 && task.isNotEmpty()) {
                    return false
                }
            }

            return true
        }

        listener<Render3DEvent.WorldRenderer.Render.Start> {
//            println("world renderer start")

            val shader = CURRENT_SHADER.valEnum.shader

            if(shader is PostProcessShader && (ShadersModule.PLAYERS.value)) {
                shader.prepareEntity()
            }
        }

        worldListener(-1) {
            if(!empty()) {
//                println("\tshaders manager listener")

                val shader = CURRENT_SHADER.valEnum.shader

                shader.begin(it.matrices)

                processPriority(1)
                processPriority(0)
                processPriority(-1)

                shader.end()

                processPriority(-2)

                shader.render()

                for(tasks in tasks.values) {
                    tasks.clear()
                }
            }
        }
    }

    fun render(
        original : Boolean = false,
        priority : Int = 0,
        block : () -> Unit
    ) {
        if(original) {
            tasks[-2]!!.add(block)
        }

        tasks[priority]!!.add(block)
    }
}