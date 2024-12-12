package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.Targetable
import lavahack.client.features.subsystem.subsystems.distanceToNearest
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.enums.Swaps
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.client.ranges.step
import lavahack.client.utils.cooldown
import lavahack.client.utils.leftClickEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.Item
import net.minecraft.item.SwordItem

/**
 * @author _kisman_
 * @since 7:57 of 31.07.2023
 */
@Module.Info(
    name = "Aura",
    description = "Hits nearest enemy around you.",
    aliases = "KillAura",
    category = Module.Category.COMBAT,
    targetable = Targetable(
        nearest = true
    )
)
object Aura : Module() {
    init {
        val weapon = register(SettingEnum("Weapon", Weapons.Sword))
        val range = register(SettingNumber("Range", 5.0, 0.0..7.0 step 0.5))
        val swing = register(Setting("Swing", true))
        val packet = register(Setting("Packet", true))
        val swap = register(SettingEnum("Swap", Swaps.None))

        tickListener {
            if(mc.player == null || mc.world == null || enemy == null) {
                return@tickListener
            }

            if(distanceToNearest <= range.value * range.value) {
                val current = mc.player!!.inventory.selectedSlot
                val slot = InventoryLocations.Hotbar.findInventoryItem(weapon.valEnum.checker)

                if((slot != -1 && (swap.valEnum != Swaps.None || current == slot))) {
                    val cooldown = cooldown(slot)

                    if(cooldown == 1f) {
                        swap.valEnum.pre(slot)

                        leftClickEntity(
                            enemy!!,
                            packet = packet.value,
                            swing = swing.value,
                            reset = true
                        )

                        swap.valEnum.post()
                    }
                }
            }
        }
    }

    enum class Weapons(
        val checker : (Item) -> Boolean
    ) {
        Any({ true }),
        Sword({ it is SwordItem }),
        Axe({ it is AxeItem }),
        Both({ it is SwordItem || it is AxeItem })
    }
}