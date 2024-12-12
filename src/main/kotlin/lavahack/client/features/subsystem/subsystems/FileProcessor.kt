package lavahack.client.features.subsystem.subsystems

import lavahack.client.LavaHack
import lavahack.client.features.config.Config
import lavahack.client.features.config.Configs
import lavahack.client.features.gui.configs.ConfigGui
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.utils.addTask

/**
 * @author _kisman_
 * @since 22:21 of 24.05.2023
 */
object FileProcessor : SubSystem(
    "File Processor"
) {
    val CONFIG_REFRESH_SETTING = Setting("Refresh", false) {
        if(it.value) {
            refreshConfigs()

            addTask {
                ConfigGui.refreshAvailableConfigs()
            }

            it.value = false
        }
    }

    val configs = mutableListOf<Config.Info>()

    private fun refreshConfigs() {
        Configs.LOADED_CONFIG = Configs.DEFAULT_FEATURES_CONFIG
        configs.clear()

        val files = LavaHack.DIRECTORY.listFiles()!!

        for(file in files) {
            val name = file.name

            if(name.endsWith(Configs.SUFFIX)) {
                val config = Config.Info(file)

                configs.add(config)
            }
        }
    }
}