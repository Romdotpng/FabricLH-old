package lavahack.client.features.module.modules.misc

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.PopListener
import lavahack.client.settings.Setting
import lavahack.client.utils.chat.ChatUtility
import lavahack.client.utils.client.interfaces.impl.*

@Module.Info(
    name = "Announcer",
    description = "um idk",
    category = Module.Category.MISC
)
class Announcer : Module() {
    init {
        val pops = register(Setting("Pops", false))
        val deaths = register(Setting("Deaths", false))

        popListener {
            if(pops.value) {
                val entity = it.entity
                val name = entity.name.string
                val popped = PopListener.pops[entity]!!

                ChatUtility.INFO.print("$name popped $popped time${formatCount(popped)}!")
            }
        }

        deathListener {
            if(deaths.value) {
                val entity = it.entity
                val name = entity.name.string
                val popped = PopListener.pops[entity]

                if(popped == null) {
                    ChatUtility.INFO.print("$name died!")
                } else {
                    ChatUtility.INFO.print("$name died after pop $popped totem${formatCount(popped)}!")
                }
            }
        }
    }

    private fun formatCount(
        count : Int
    ) = if(count > 1) "s" else ""
}