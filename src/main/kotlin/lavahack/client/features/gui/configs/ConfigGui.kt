package lavahack.client.features.gui.configs

import lavahack.client.features.config.Configs
import lavahack.client.features.gui.LavaHackScreen
import lavahack.client.features.gui.modules.Frame
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.gui.modules.component.IComponent
import lavahack.client.features.subsystem.subsystems.FileProcessor

/**
 * @author _kisman_
 * @since 14:32 of 21.05.2023
 */
object ConfigGui : LavaHackScreen(
    "Configs"
) {
    val CONFIG_FRAMES = mutableListOf<Frame>()
    val CONFIG_SAVER_FRAME = Frame(emptyList(), "Config Saver", 10.0, 10.0)
    val CONFIG_LOADER_FRAME = Frame(emptyList(), "Config Loader", 10.0 + 120 + 10, 10.0)

    val availableConfigs = mutableListOf<IComponent>()

    fun create() {
        ModuleGui.addModuleComponents(CONFIG_SAVER_FRAME, Configs.features, layerOffset = -1)
        ModuleGui.addSettingComponents(CONFIG_SAVER_FRAME, listOf(Configs.CONFIG_NAME, Configs.SAVE_SETTING), layerOffset = -1)
        ModuleGui.addSettingComponents(CONFIG_LOADER_FRAME, listOf(FileProcessor.CONFIG_REFRESH_SETTING), layerOffset = -1)
        ModuleGui.addSettingComponents(CONFIG_LOADER_FRAME, FileProcessor.configs, layerOffset = -1, adder = { availableConfigs.add(it!!) })

        CONFIG_FRAMES.add(CONFIG_SAVER_FRAME)
        CONFIG_FRAMES.add(CONFIG_LOADER_FRAME)
    }

    fun refreshAvailableConfigs() {
        CONFIG_LOADER_FRAME.components.removeAll(availableConfigs)

        ModuleGui.addSettingComponents(CONFIG_LOADER_FRAME, FileProcessor.configs, layerOffset = -1, adder = { availableConfigs.add(it!!) })
    }

    override fun onOpen() {
        ModuleGui.frames = CONFIG_FRAMES
    }
}