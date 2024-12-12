package lavahack.client

import com.mojang.brigadier.CommandDispatcher
import lavahack.client.event.bus.EventBus
import lavahack.client.features.command.Commands
import lavahack.client.features.config.Configs
import lavahack.client.features.friend.Friends
import lavahack.client.features.gui.configs.ConfigGui
import lavahack.client.features.gui.huds.HudEditor
import lavahack.client.features.gui.modules.ModuleGui
import lavahack.client.features.hud.Huds
import lavahack.client.features.module.Modules
import lavahack.client.features.subsystem.SubSystems
import lavahack.client.utils.mc
import lavahack.client.utils.minecraft.LavaHackCommandSource
import lavahack.client.utils.minecraft.LavaHackResourceFactory
import lavahack.client.utils.minecraft.LavaHackResourceManager
import lavahack.client.utils.minecraft.LavaHackResourcePack
import net.fabricmc.api.ModInitializer
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*

@Suppress("UNUSED")
object LavaHack : ModInitializer {
    const val NAME = "LavaHack"
    const val VERSION = "1.0"
    const val MODID = "lavahack"
    const val PREFIX = "!"
    const val DEBUG = true
    val LOGGER = LogManager.getLogger("LavaHack")!!
    val EVENT_BUS = EventBus()
    val RESOURCE_PACK = LavaHackResourcePack()
    val RESOURCE_FACTORY = LavaHackResourceFactory()
    val RESOURCE_MANAGER = LavaHackResourceManager()
    val COMMAND_SOURCE = LavaHackCommandSource()
    val COMMAND_DISPATCHER = CommandDispatcher<CommandSource>()


    val DIRECTORY = File(mc.runDirectory, NAME.lowercase(Locale.getDefault()))
        get() {
            if(!field.exists()) {
                field.mkdir()
            }

            return field
        }

    override fun onInitialize() {
        val timestamp = System.currentTimeMillis()

        LOGGER.info("Initializing LavaHack")

        SubSystems.preinit()

        Friends.init()
        Modules.init()
        Huds.init()
        Commands.init()
        SubSystems.init()

        Configs.init()
        Configs.load()

        ModuleGui.create()
        HudEditor.create()
        ConfigGui.create()

        LOGGER.info("Initialized LavaHack! It took ${System.currentTimeMillis() - timestamp} ms!")
    }
}
