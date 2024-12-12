package lavahack.client.features.module.modules.client

import lavahack.client.features.gui.searchbar.SearchBar
import lavahack.client.features.gui.selectionbar.SelectionBar
import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.*
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register

/**
 * @author _kisman_
 * @since 10:50 of 24.06.2023
 */
@Module.Info(
    name = "Settings",
    category = Module.Category.CLIENT,
    visible = false,
    state = true,
    toggleable = false,
    properties = Module.Properties(
        bind = false,
        visible = false
    )
)
class Settings : Module() {
    init {
        fun register(
            registry : ISettingRegistry,
            name : String,
            prefix : String
        ) {
            val group = register(SettingGroup(name))

            register(group.add(registry))

            group.prefix(prefix)
        }

        register(CrystalPlacementController, "Crystals", "Crystal Placement")
        register(BlockPlacementController, "Blocks", "Block Placement")
        register(HoleProcessor, "Holes", "Hole Processor")
        register(RotationSystem, "Rotate", "Rotation System")
        register(GrimAnticheat, "Grim", "Grim Anticheat")
        register(ColorManager, "Colors", "Color Manager")
        register(FontController, "Fonts", "Font Controller")
        register(CoreShadersController, "Shaders", "Shaders Controller")
        register(PostprocessShaderRenderer, "Postprocess Shaders", "Shader Renderer")
        register(DevelopmentSettings, "Development", "Development Environment")
        register(SelectionBar, "Selection Bar", "Selection Bar")
        register(SearchBar, "Search Bar", "Search Bar")
        register(ScreenAnimator, "Screen Animations", "Screen Animator")
        register(ScreenBackground, "Screen Background", "Screen Background")
//        register(BlurController, "Blur", "Blur Controller")
        register(GaussianBlurRenderer, "Blur", "Gaussian Blur Renderer")
    }
}