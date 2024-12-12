package lavahack.client.features.module

import lavahack.client.LavaHack
import lavahack.client.features.module.modules.client.UIModule
import lavahack.client.features.module.modules.client.Settings
import lavahack.client.features.module.modules.combat.*
import lavahack.client.features.module.modules.debug.*
import lavahack.client.features.module.modules.exploit.*
import lavahack.client.features.module.modules.misc.*
import lavahack.client.features.module.modules.movement.*
import lavahack.client.features.module.modules.player.*
import lavahack.client.features.module.modules.render.*

/**
 * @author _kisman_
 * @since 4:02 of 08.05.2023
 */
object Modules {
    val modules = mutableListOf<Module>()
    val names = mutableMapOf<String, Module>()

    fun init() {
        LavaHack.LOGGER.info("Initializing modules")

        //client
        add(Settings())
        add(UIModule())

        //combat
        add(Aura)
        add(AutoArmor())
        add(AutoCrystal())
        add(AutoTrap())
        add(AutoXP())
        add(Blocker())
        add(CevBreaker())
        add(FeetPlace())
        add(HoleFill())
        add(Offhand())
        add(SelfFill())
        add(SelfTrap())

        //debug
        add(AutoMoveTest())
        add(BreakProgressEmulator())
        add(DebugPacketLogger())
        add(IDPredict())
        add(MotionLogger())
        add(NanoVGTest())
        add(PacketTester())
        add(RaycastTest())
        add(RenderTest())
        add(ShaderTest())
        add(SneakTest())
        add(StrictDirectionTest())

        //exploit
        add(CornerClip())
        add(FakePearl())
        add(HitboxDesync())
        add(MultiTask)
        add(PacketMine)
        add(PeekAssist())
        add(RaytraceBypass())

        //misc
        add(Announcer())
        add(EnderChestMiner())
        add(FreeLook)
        add(MiddleClickPearl())
        add(SuperSecret())
        add(Triangulator())
        add(XCarry())

        //movement
        add(Anchor())
        add(AutoWalk())
        add(FastFall())
        add(NoSlow)
        add(Phase)
        add(Speed)
        add(Sprint())
        add(Step)

        //player
        add(AutoEat())
        add(AutoRespawn())
        add(Criticals())
        add(FakePlayer())
        add(FastInteract())
        add(NoEntityTrace)
        add(Replenish())
        add(Velocity())
        add(Warp())

        //render
        add(BlockHighlight())
        add(Chams())
        add(ESP())
        add(FovModifier())
        add(FullBright)
        add(InventoryInfo)
        add(Nametags())
        add(NoRender())
        add(ShadersModule)
        add(Trails())
        add(ViewClip)
        add(ViewModel)
    }

    private fun add(
        module : Module
    ) {
        module.post()

        modules.add(module)
        names[module.info.name] = module

        if(!module.info.submodule) {
            module.info.category.modules.add(module)
        }

        for(submodule in module.submodules) {
            add(submodule)
        }
    }
}