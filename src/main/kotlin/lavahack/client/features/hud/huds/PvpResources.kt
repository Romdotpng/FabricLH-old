package lavahack.client.features.hud.huds

import lavahack.client.features.hud.Hud
import lavahack.client.features.module.Module
import lavahack.client.settings.types.SettingEnum
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.countItems
import net.minecraft.item.Item
import net.minecraft.item.Items

private val ITEMS = mapOf(
    0 to SettingEnum("First Item", PvpResources.AvailableItems.Crystal, "1st"),
    1 to SettingEnum("Second Item", PvpResources.AvailableItems.Totem, "2nd"),
    2 to SettingEnum("Third Item", PvpResources.AvailableItems.Gapple, "3rd"),
    3 to SettingEnum("Fourth Item", PvpResources.AvailableItems.XP, "4th")
)

@Suppress("unused")
@Module.Info(
    name = "PvpResources",
    description = "Shows count of items useful for pvp"
)
class PvpResources : Hud.ItemList(
    { ITEMS[it]!!.valEnum.items.isNotEmpty() },
    {
        val item = ITEMS[it]!!.valEnum.items.first()

        //TODO: rewrite usage of countItem method
        item to (countItems(item, InventoryLocations.Inventory) + countItems(item, InventoryLocations.Offhand) + countItems(item, InventoryLocations.Hotbar)).toString()
    }
) {
    init {
        for(setting in ITEMS.values) {
            register(setting)
        }
    }

    enum class AvailableItems(
        vararg val items : Item
    ) {
        None,
        Crystal(Items.END_CRYSTAL),
        Totem(Items.TOTEM_OF_UNDYING),
        Gapple(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE),
        XP(Items.EXPERIENCE_BOTTLE),
        Arrow(Items.ARROW),
        Obby(Items.OBSIDIAN),
        EnderChest(Items.ENDER_CHEST),
        Anvil(Items.ANVIL, Items.CHIPPED_ANVIL, Items.DAMAGED_ANVIL),
        Anchor(Items.RESPAWN_ANCHOR),
        Glowstone(Items.GLOWSTONE)
    }
}