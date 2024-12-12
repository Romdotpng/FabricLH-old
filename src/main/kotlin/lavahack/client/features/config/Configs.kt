package lavahack.client.features.config

import lavahack.client.LavaHack
import lavahack.client.features.config.configs.features.HudAnchorsConfig
import lavahack.client.features.config.configs.features.HudEditorConfig
import lavahack.client.features.config.configs.features.HudsConfig
import lavahack.client.features.config.configs.properties.FriendsConfig
import lavahack.client.features.config.configs.features.ModulesConfig
import lavahack.client.features.subsystem.subsystems.AccountController
import lavahack.client.settings.Setting
import lavahack.client.utils.chat.ChatUtility
import java.io.File
import java.nio.file.Files

/**
 * @author _kisman_
 * @since 13:29 of 21.05.2023
 */
object Configs {
    const val DEFAULT_FEATURES_CONFIG_NAME = "default"
    const val DEFAULT_PROPERTIES_CONFIG_NAME = "config"
    const val SUFFIX = ".lavahack"

    val DEFAULT_FEATURES_CONFIG = Config.Info(File(LavaHack.DIRECTORY, "$DEFAULT_FEATURES_CONFIG_NAME$SUFFIX"), Config.Info.Data("default", AccountController.DATA.name, -1L))
    val DEFAULT_PROPERTIES_CONFIG = Config.Info(File(LavaHack.DIRECTORY,"$DEFAULT_PROPERTIES_CONFIG_NAME$SUFFIX"), Config.Info.Data("default", AccountController.DATA.name, -1L))
    var LOADED_CONFIG = DEFAULT_FEATURES_CONFIG

    val configs = mutableListOf<Config>()
    val features = mutableListOf<Config>()
    val properties = mutableListOf<Config>()

    val CONFIG_NAME = Setting("Name", "test2")

    val SAVE_SETTING = Setting("Save", false) {
        if(it.value) {
            save(Config.Info.byName(CONFIG_NAME.value), false, features)

            it.value = false
        }
    }

    fun init() {
        fun addFeaturesConfig(
            config : Config
        ) {
            configs.add(config)
            features.add(config)
        }

        fun addPropertiesConfig(
            config : Config
        ) {
            configs.add(config)
            properties.add(config)
        }

        //TODO: rewrite it
        addFeaturesConfig(ModulesConfig)
        addFeaturesConfig(HudsConfig)
        addFeaturesConfig(HudAnchorsConfig)
        addFeaturesConfig(HudEditorConfig)
        addPropertiesConfig(FriendsConfig)

        Runtime.getRuntime().addShutdownHook(Thread { save() })
    }

    fun save() {
        save(DEFAULT_FEATURES_CONFIG, true, features)
        save(DEFAULT_PROPERTIES_CONFIG, true, properties)
    }

    fun save(
        info : Config.Info,
        default : Boolean,
        configs : List<Config>
    ) {
        val file = info.file
        val name = info.data.name
        val lines = mutableListOf(
            "author=${info.data.author}",
            "timestamp=${info.data.timestamp}"
        )

        if(!file.exists()) {
            Files.createFile(file.toPath())
        }

        for (config in configs) {
            if (config.state || default) {
                config.save(default)

                for (data in config.datas.values) {
                    for (entry in data.entries) {
                        val line = "${data.prefix}.${entry.key}=${entry.value}"

                        lines.add(line)
                    }
                }
            }
        }

        Files.write(file.toPath(), lines)
        ChatUtility.INFO.print("Successfully saved $name config!")
    }

    fun load(
        features : Boolean = true,
        properties : Boolean = true
    ) {
        if(features) {
            load(DEFAULT_FEATURES_CONFIG)
        }

        if(properties) {
            load(DEFAULT_PROPERTIES_CONFIG)
        }
    }

    fun load(
        info : Config.Info
    )  {
        val file = info.file
        val name = info.data.name

        if(file.exists()) {
            parse(file)

            LOADED_CONFIG = info

            ChatUtility.INFO.print("Successfully loaded $name config!")
        }
    }

    fun parse(
        file : File
    ) {
        fun findConfig(
            prefix : String
        ) : Config? {
            val split1 = prefix.split(".")

            return if(split1.size >= 2) {
                when(split1[0]) {
                    "module" -> ModulesConfig
                    "hud" -> HudsConfig
                    "friend" -> FriendsConfig
                    "hudeditor" -> HudEditorConfig
                    else -> null
                }
            } else {
                null
            }
        }

        for(config in configs) {
            config.datas.clear()
        }

        val lines = file.readLines()

        for(line in lines) {
            if(!line.startsWith("author") && !line.startsWith("timestamp")) {
                val split1 = line.split("=")
                val split2 = split1[0].split(".")
                val prefix = split1[0].removeSuffix(".${split2[split2.size - 1]}")
                val key = split1[0].removePrefix("$prefix.")
                val value = split1[1]
                val config = findConfig(prefix)

                if (config != null) {
                    val data = config.datas[prefix]

                    if (data == null) {
                        config += StoredData(prefix, key, value)
                    } else {
                        data[key] = value
                    }
                }
            }
        }

        for(config in configs) {
            config.load()
        }
    }
}