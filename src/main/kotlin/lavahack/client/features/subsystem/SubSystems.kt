package lavahack.client.features.subsystem

import lavahack.client.LavaHack
import lavahack.client.features.subsystem.subsystems.*

/**
 * @author _kisman_
 * @since 3:49 of 08.05.2023
 */
object SubSystems {
    private val subsystems = listOf(
        AccountController,
        BlockPlacementController,
        BlockUpdateListener,
        BlurController,
        CodeCallerController,
        ColorManager,
        CoreShadersController,
        CrystalPlacementController,
        DeathListener,
        DefaultAnimatorController,
        DevelopmentSettings,
        EnemyManager,
//        EnvironmentManager,
        FileProcessor,
        FontController,
        GaussianBlurRenderer,
        GrimAnticheat,
        HoleProcessor,
        HudCorrector,
        InputController,
        InventoryManager,
        NanoVGRenderer,
        PlayerManager,
        PopListener,
        RotationSystem,
        ScreenAnimator,
        ScreenBackground,
        SelectedScreenManager,
        PostprocessShaderRenderer,
        TimerManager
    )

    fun preinit() {
        LavaHack.LOGGER.info("Preinitializing subsystems")

        for(subsystem in subsystems) {
            if(subsystem.hasPreinitializer) {
                subsystem.preinit()
            }
        }
    }

    fun init() {
        LavaHack.LOGGER.info("Initializing subsystems")

        for(subsystem in subsystems) {
            subsystem.init()
            subsystem.listeners.subscribe()
        }
    }
}