package lavahack.client.features.hud

import lavahack.client.LavaHack
import lavahack.client.features.hud.huds.*
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.SettingRegistry

/**
 * @author _kisman_
 * @since 19:18 of 08.05.2023
 */
object Huds : ISettingRegistry {
    override val registry = SettingRegistry()

    val huds = mutableListOf<Hud>()
    val names = mutableMapOf<String, Hud>()

    fun init() {
        LavaHack.LOGGER.info("Initializing huds")
        add(Armor())
        add(ArrayList())
        add(CrystalsPerSecond())
        add(Fps())
        add(Ping())
        add(PvpResources())
        add(Speedometer())
        add(Watermark())
        add(Welcomer())
    }

    private fun add(
        hud : Hud
    ) {
        hud.post()

        huds.add(hud)
        names[hud.info.name] = hud
    }
}