package lavahack.client.features.module.modules.render

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.SettingRegistry
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import net.minecraft.util.Arm

/**
 * @author _kisman_
 * @since 14:45 of 11.07.2023
 */
@Suppress("PrivatePropertyName", "PropertyName")
@Module.Info(
    name = "ViewModel",
    description = "Modifies held item renderer",
    category = Module.Category.RENDER
)
object ViewModel : Module() {
    private val ITEM_FOV_GROUP = register(SettingGroup("Item Fov"))

    val ITEM_FOV_STATE = register(ITEM_FOV_GROUP.add(Setting("State", false)))
    val ITEM_FOV_VALUE = register(ITEM_FOV_GROUP.add(SettingNumber("Value", 150, 100..200)))


    private val ITEMS_GROUP = register(SettingGroup("Items"))

    val LEFT_ITEM = register(ITEMS_GROUP.add(HandSettings(Arm.LEFT)))
    val RIGHT_ITEM = register(ITEMS_GROUP.add(HandSettings(Arm.RIGHT)))


    private val HANDS_GROUP = register(SettingGroup("Hands"))

    val LEFT_HAND = register(HANDS_GROUP.add(HandSettings(Arm.LEFT)))
    val RIGHT_HAND = register(HANDS_GROUP.add(HandSettings(Arm.RIGHT)))

    /*private val CUSTOM_SWING_GROUP = register(SettingGroup("Custom Swing"))

    val CUSTOM_SWING_STATE = register(CUSTOM_SWING_GROUP.add(Setting("State", false)))

    private val CUSTOM_SWING_ITEMS_GROUP = register(CUSTOM_SWING_GROUP.add(SettingGroup("Items")))

    val CUSTOM_SWING_SWORD = register(CUSTOM_SWING_ITEMS_GROUP.add(Setting("Sword", true)))
    val CUSTOM_SWING_PICKAXE = register(CUSTOM_SWING_ITEMS_GROUP.add(Setting("Sword", true)))
    val CUSTOM_SWING_OTHERS = register(CUSTOM_SWING_ITEMS_GROUP.add(Setting("Others", true)))*/

    init {
        ITEMS_GROUP.prefix("Item")
        HANDS_GROUP.prefix("Hand")
//        CUSTOM_SWING_GROUP.prefix("Custom Swing")
//        CUSTOM_SWING_ITEMS_GROUP.prefix("Items")
    }

    class HandSettings(
        arm : Arm
    ) : ISettingRegistry {
        override val registry = SettingRegistry()

        private val GROUP = register(SettingGroup(arm.toString()))

        private val SCALES_GROUP = register(GROUP.add(SettingGroup("Scales")))

        val SCALE_X = register(SCALES_GROUP.add(SettingNumber("X", 1f, 0f..4f)))
        val SCALE_Y = register(SCALES_GROUP.add(SettingNumber("Y", 1f, 0f..4f)))
        val SCALE_Z = register(SCALES_GROUP.add(SettingNumber("Z", 1f, 0f..4f)))

        private val TRANSLATES_GROUP = register(GROUP.add(SettingGroup("Translates")))

        val TRANSLATE_X = register(TRANSLATES_GROUP.add(SettingNumber("X", 0.0, -2.0..2.0)))
        val TRANSLATE_Y = register(TRANSLATES_GROUP.add(SettingNumber("Y", 0.0, -2.0..2.0)))
        val TRANSLATE_Z = register(TRANSLATES_GROUP.add(SettingNumber("Z", 0.0, -2.0..2.0)))

        private val ROTATES_GROUP = register(GROUP.add(SettingGroup("Rotates")))

        val ROTATE_X = register(ROTATES_GROUP.add(SettingNumber("X", 0, 0..360)))
        val ROTATE_Y = register(ROTATES_GROUP.add(SettingNumber("Y", 0, 0..360)))
        val ROTATE_Z = register(ROTATES_GROUP.add(SettingNumber("Z", 0, 0..360)))

        init {
            SCALES_GROUP.prefix("Scale")
            TRANSLATES_GROUP.prefix("Translate")
            ROTATES_GROUP.prefix("Rotate")
            GROUP.prefix(arm.toString())
        }
    }
}