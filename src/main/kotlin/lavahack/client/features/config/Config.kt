package lavahack.client.features.config

import lavahack.client.LavaHack
import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.AccountController
import lavahack.client.settings.Setting
import java.io.File

/**
 * @author _kisman_
 * @since 13:29 of 21.05.2023
 */
@Suppress("UNUSED_PARAMETER")
open class Config : Module() {
    val datas = mutableMapOf<String, StoredData>()

    operator fun plusAssign(
        data : StoredData
    ) {
        datas[data.prefix] = data
    }

    open fun save(
        default : Boolean
    ) {
        datas.clear()
    }

    open fun load() {

    }

    class Info(
        val file : File,
        val data : Data = Data.parse(file)
    ) : Setting<Boolean>(
        data.name,
        false
    ) {
        override var value : Boolean
            get() = Configs.LOADED_CONFIG == this
            set(value) {
                if(Configs.LOADED_CONFIG != this) {
                    Configs.load(this)
                }
            }

        companion object {
            fun byName(
                name : String
            ) = Info(File(LavaHack.DIRECTORY, "$name${Configs.SUFFIX}"), Data(name, AccountController.DATA.name, System.currentTimeMillis()))
        }

        class Data(
            val name : String,
            val author : String,
            val timestamp : Long
        ) {
            companion object {
                fun parse(
                    file : File
                ) : Data {
                    val lines = file.readLines()

                    val name = file.name.removeSuffix(Configs.SUFFIX)
                    var author = "NULL"
                    var timestamp = -1L

                    for(line in lines) {
                        val split1 = line.split("=")

                        when(split1[0]) {
                            "author" -> author = split1[0]
                            "timestamp" -> timestamp = split1[0].toLongOrNull() ?: -1L
                        }

                        if(author != "NULL" && timestamp != -1L) {
                            break
                        }
                    }

                    return Data(name, author, timestamp)
                }
            }
        }
    }
}