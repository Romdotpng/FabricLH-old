package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.*
import lavahack.client.features.module.modules.exploit.PacketMine
import lavahack.client.features.subsystem.subsystems.CrystalPlacementController
import lavahack.client.features.subsystem.subsystems.RotationSystem
import lavahack.client.features.subsystem.subsystems.Targetable
import lavahack.client.mixins.AccessorPlayerMoveC2SPacket
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.SlideRenderingPattern
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.client.enums.Hands
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.enums.Swaps
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.client.ranges.step
import lavahack.client.utils.entity.EntityID
import lavahack.client.utils.render.world.SlideRenderer
import lavahack.client.utils.threads.delayedTask
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.roundToInt

/**
 * @author _kisman_
 * @since 14:35 of 21.06.2023
 */
@Suppress("DEPRECATION", "unused")
@Module.Info(
    name = "AutoCrystal",
    description = "Automatically kills your enemies with the best way",
    aliases = "CrystalAura, Crystal",
    category = Module.Category.COMBAT,
    targetable = Targetable(
        nearest = true
    )
)
class AutoCrystal : Module() {
    init {
        val swap = register(SettingEnum("Swap", Swaps.None))
        val hand = register(SettingEnum("Hand", Hands.Offhand))

        val placeGroup = register(SettingGroup("Place"))
        val placeState = register(placeGroup.add(Setting("State", true)))
        val placeRange = register(placeGroup.add(SettingNumber("Range", 5.0, 4.0..6.0 step 0.5)))
        val placeDelay = register(placeGroup.add(SettingNumber("Delay", 0L, 0L..500L step 10L)))
        val placePacket = register(placeGroup.add(Setting("Packet", true)))
        val placeSwing = register(placeGroup.add(Setting("Swing", true)))

        val breakGroup = register(SettingGroup("Break"))
        val breakState = register(breakGroup.add(Setting("State", true)))
        val breakTiming = register(breakGroup.add(SettingEnum("Timing", BreakTimings.Adaptive)))
        val breakRange = register(breakGroup.add(SettingNumber("Range", 5.0, 4.0..6.0 step 0.5)))
        val breakDelay = register(breakGroup.add(SettingNumber("Delay", 0L, 0L..500L step 10L)))
        val breakSequentialDelay = register(breakGroup.add(SettingNumber("Sequential Delay", 2, 0..10)))
        val breakPacket = register(breakGroup.add(Setting("Packet", true)))
        val breakSwing = register(breakGroup.add(Setting("Swing", true)))
        val breakSetDead = register(breakGroup.add(Setting("Set Dead", false)))
        val breakRemoveEntity = register(breakGroup.add(Setting("Remove Entity", false)))
        val breakIgnoreEntity = register(breakGroup.add(Setting("Ignore Entity", false)))
        val breakRotates = register(breakGroup.add(SettingEnum("Rotate At", BreakRotates.None)))

        val instaplaceGroup = register(SettingGroup("Instaplace"))
        val instaplaceOnRemove = register(instaplaceGroup.add(Setting("On Remove", false)))
        val instaplaceOnBreak = register(instaplaceGroup.add(Setting("On Break", false)))
        val instaplaceOnInstabreak = register(instaplaceGroup.add(Setting("On Instabreak", false)))
        val instaplaceOnInstacalc = register(instaplaceGroup.add(Setting("On Instacalc", false)))
        val instaplaceDelay = register(instaplaceGroup.add(SettingNumber("Delay", 0L, 0L..100L)))

        val instabreakGroup = register(SettingGroup("Instabreak"))
        val instabreakState = register(instabreakGroup.add(Setting("State", false)))
        val instabreakSetDead = register(instabreakGroup.add(Setting("Set Dead", false)))
        val instabreakRemoveEntity = register(instabreakGroup.add(Setting("Remove Entity", false)))
        val instabreakIgnoreEntity = register(instabreakGroup.add(Setting("Ignore Entity", false)))
        val instabreakDelay = register(instabreakGroup.add(SettingNumber("Delay", 0L, 0L..100L)))

        val instacalcGroup = register(SettingGroup("Instacalc"))
        val instacalcState = register(instacalcGroup.add(Setting("State", false)))

        val idPredictGroup = register(SettingGroup("ID Predict"))
        val idPredictState = register(idPredictGroup.add(Setting("State", false)))
        val idPredictAdaptive = register(idPredictGroup.add(Setting("Adaptive", false)))
        val idPredictMinRange = register(idPredictGroup.add(SettingNumber("Min Range", 1, 0..30)))
        val idPredictMaxRange = register(idPredictGroup.add(SettingNumber("Max Range", 10, 0..30)))
        val idPredictDebug = register(idPredictGroup.add(Setting("Debug", false)))

        val antiDesyncGroup = register(SettingGroup("Anti Desync"))
        val antiDesyncOnPlace = register(antiDesyncGroup.add(Setting("On Place", false)))
        val antiDesyncOnInstaplace = register(antiDesyncGroup.add(Setting("On Instaplace", false)))
        val antiDesyncAwait = register(antiDesyncGroup.add(SettingNumber("Await", 120L, 0L..1000L)))
        val antiDesyncSmartAwait = register(antiDesyncGroup.add(Setting("Smart Await", false)))

        val calculationGroup = register(SettingGroup("Calculation"))
        val calculationDelay = register(calculationGroup.add(SettingNumber("Delay", 0L, 0L..100L)))
        val calculationPredict = register(calculationGroup.add(Setting("Predict", false)))
        val calculationTerrain = register(calculationGroup.add(Setting("Terrain", true)))
        val calculationPacketMineSync = register(calculationGroup.add(Setting("Packet Mine Sync", false)))

        val damagesGroup = register(calculationGroup.add(SettingGroup("Damages")))
        val selfDamageGroup = register(damagesGroup.add(SettingGroup("Self")))
        val selfDamageLogic = register(selfDamageGroup.add(SettingEnum("Logic", SelfDamageLogics.Custom)))
        val selfDamageValue = register(selfDamageGroup.add(SettingNumber("Max", 10, 0..37)))
        val enemyDamageGroup = register(damagesGroup.add(SettingGroup("Enemy")))
        val enemyDamageValue = register(enemyDamageGroup.add(SettingNumber("Min", 7, 0..37)))

        val renderGroup = register(SettingGroup("Render"))
        val pattern = register(renderGroup.add(SlideRenderingPattern()))

        placeGroup.prefix("Place")
        breakGroup.prefix("Break")
        instaplaceGroup.prefix("Instaplace")
        instabreakGroup.prefix("Instabreak")
        instacalcGroup.prefix("Instacalc")
        idPredictGroup.prefix("ID Predict")
        antiDesyncGroup.prefix("Anti Desync")
        damagesGroup.prefix("Damages")
        calculationGroup.prefix("Calculation")
        renderGroup.prefix("Render")

        selfDamageGroup.prefix("Self")
        enemyDamageGroup.prefix("Enemy")

        val renderer = SlideRenderer()

        val placeTimer = Stopwatch()
        val breakTimer = Stopwatch()
        val calculationTimer = Stopwatch()
        val antiDesyncTimer = Stopwatch()

        var toPlace : PlaceInfo? = null
        var toBreak : EndCrystalEntity? = null

        val placed = mutableListOf<PlaceInfo>()
        val removed = mutableSetOf<Int>()
        var hasPlaced : BlockPos? = null

        var offsetID = 0
        var predictedID = 0

        var currentYaw = -1f
        var currentPitch = -1f

        breakIgnoreEntity.onChange = { removed.clear() }
        instabreakIgnoreEntity.onChange = { removed.clear() }

        fun reset() {
            renderer.reset()
            toPlace = null
            toBreak = null
            placed.clear()
            removed.clear()
            hasPlaced = null
            currentYaw = -1f
            currentPitch = -1f
        }

        fun doPlace(
            instaplace : Boolean
        ) {
            if(toPlace != null && (!instaplace || hasPlaced == null || hasPlaced != toPlace!!.pos) && swap.valEnum != Swaps.None || mc.player!!.mainHandStack.item == Items.END_CRYSTAL || mc.player!!.offHandStack.item == Items.END_CRYSTAL) {
                val slot = InventoryLocations.Hotbar.findInventoryItem(Items.END_CRYSTAL)

                if(slot != -1 || mc.player!!.offHandStack.item == Items.END_CRYSTAL) {
                    swap.valEnum.pre(slot)

                    rightClickBlock(
                        toPlace!!.pos,
                        hand = hand.valEnum.hand,
                        packet = placePacket.value,
                        swing = placeSwing.value,
                        rotate = CrystalPlacementController.PLACE_ROTATE.valEnum,
                        strictDirection = CrystalPlacementController.STRICT_DIRECTION.value
                    )

                    if((!instaplace && antiDesyncOnPlace.value) || (instaplace && antiDesyncOnInstaplace.value)) {
                        placed.add(toPlace!!)
                    }

                    swap.valEnum.post()

                    placeTimer.reset()

                    if(!instaplace) {
                        hasPlaced = toPlace!!.pos
                    }
                }
            }
        }

        fun doBreak(
            nullable : Entity? = null
        ) {
            val entity = nullable ?: toBreak!!

            leftClickEntity(
                entity,
                hand = hand.valEnum.hand,
                packet = breakPacket.value,
                swing = breakSwing.value,
                rotate = CrystalPlacementController.BREAK_ROTATE.valEnum
            )

            antiDesyncTimer.reset()
            placed.clear()

            //TODO: add check if we broke crystal from hasPlaced
            hasPlaced = null
        }

        fun calculatePlace() {
            if(placeState.value) {
                var maxDamage = 0.0
                var minDamage = Double.MAX_VALUE

                val antiDesync = (antiDesyncOnPlace.value || antiDesyncOnInstaplace.value) && antiDesyncTimer.passed(if(antiDesyncSmartAwait.value) ping() * 2L else antiDesyncAwait.value)
                val exclusions = mutableSetOf<BlockPos>()

                if(antiDesync) {
                    for(info in placed) {
                        if(!info.antiDesync || toPlace?.antiDesync == true) {
                            val pos = info.pos

                            exclusions.add(pos)
                            exclusions.add(pos.add(1, 0, 0))
                            exclusions.add(pos.add(0, 0, 1))
                            exclusions.add(pos.add(-1, 0, 0))
                            exclusions.add(pos.add(0, 0, -1))
                            exclusions.add(pos.add(1, 0, -1))
                            exclusions.add(pos.add(-1, 0, 1))
                            exclusions.add(pos.add(1, 0, 1))
                            exclusions.add(pos.add(-1, 0, -1))
                            exclusions.add(pos.add(1, 1, 0))
                            exclusions.add(pos.add(0, 1, 1))
                            exclusions.add(pos.add(-1, 1, 0))
                            exclusions.add(pos.add(0, 1, -1))
                            exclusions.add(pos.add(1, 1, -1))
                            exclusions.add(pos.add(-1, 1, 1))
                            exclusions.add(pos.add(1, 1, 1))
                            exclusions.add(pos.add(-1, 1, -1))
                        }
                    }
                }

                toPlace = null

                sphere(mc.player!!.blockPos, placeRange.value.roundToInt()) {
                    if((!antiDesync || !exclusions.contains(it)) && crystalable(it, removed = removed)) {
                        val state = Blocks.AIR.defaultState
                        val prev = it.state()
                        var willBeBroken = false

                        if(calculationPacketMineSync.value && PacketMine.data != null) {
                            val data = PacketMine.data!!
                            val pos = data.pos
                            val slot = PacketMine.slot

                            if(pos.down() == it) {
                                val time = miningTime(it, mc.player!!.inventory.getStack(slot))
                                val delta = time - (System.currentTimeMillis() - data.start)
                                val ping = ping()

                                if(delta <= ping) {
                                    willBeBroken = true

                                    mc.world!!.setBlockState(it, state)
                                }
                            }
                        }

                        val enemyDamage = damageByCrystal(it, enemy!!, calculationPredict.value, calculationTerrain.value)

                        if(enemyDamage > maxDamage && enemyDamage >= enemyDamageValue.value) {
                            val selfDamage = damageByCrystal(it, mc.player!!, calculationPredict.value, false)

                            if((selfDamageLogic.valEnum == SelfDamageLogics.AntiSuicide && selfDamage < mc.player!!.health + mc.player!!.absorptionAmount) || (selfDamage <= minDamage && selfDamage <= selfDamageValue.value)) {
                                toPlace = PlaceInfo(it, antiDesync, willBeBroken)
                                maxDamage = enemyDamage
                                minDamage = selfDamage

                                if(willBeBroken) {
                                    mc.world!!.setBlockState(it, prev)
                                }
                            }
                        }
                    }
                }

                if(toPlace?.willBeBroken == true) {
                    doPlace(false)
                }
            }
        }

        fun calculateBreak() {
            if(breakState.value) {
                toBreak = null

                var maxDamage = 0.0
                var minDamage = Double.MAX_VALUE

                for (entity in mc.world!!.entities) {
                    if (entity is EndCrystalEntity && mc.player!!.pos distanceSq entity.pos <= breakRange.value * breakRange.value && (breakTiming.valEnum == BreakTimings.Adaptive || entity.endCrystalAge >= breakSequentialDelay.value)) {
                        val enemyDamage = damageByCrystal(entity, enemy!!, calculationPredict.value, calculationTerrain.value)

                        if (enemyDamage > maxDamage && enemyDamage >= enemyDamageValue.value) {
                            val selfDamage = damageByCrystal(entity, mc.player!!, calculationPredict.value, calculationTerrain.value)

                            if((selfDamageLogic.valEnum == SelfDamageLogics.AntiSuicide && selfDamage < mc.player!!.health + mc.player!!.absorptionAmount) || (selfDamage <= minDamage && selfDamage <= selfDamageValue.value)) {
                                toBreak = entity
                                maxDamage = enemyDamage
                                minDamage = selfDamage
                            }
                        }
                    }
                }
            }
        }

        enableCallback {
            reset()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                reset()

                return@tickListener
            }

            if(enemy == null) {
                toPlace = null
                toBreak = null

                return@tickListener
            }

            if(toBreak != null && !toBreak!!.isAlive) {
                toBreak = null
            }

            if(calculationTimer.passed(calculationDelay.value, true)) {
                calculatePlace()
                calculateBreak()
            }

            if(placeState.value && toPlace != null && placeTimer.passed(placeDelay.value)) {
                doPlace(false)

                if(antiDesyncOnPlace.value) {
                    placed.add(toPlace!!)
                }
            }

            if(breakState.value && toBreak != null && breakTimer.passed(breakDelay.value, true)) {
                doBreak()

                handleSetDead(
                    toBreak!!,
                    breakSetDead.value,
                    breakRemoveEntity.value
                )

                if(breakIgnoreEntity.value) {
                    val id = toBreak!!.id

                    removed.add(id)
                }

                if(instaplaceOnBreak.value && toPlace != null) {
                    delayedTask(instaplaceDelay.value) {
                        doPlace(true)
                    }
                }
            }

            when(breakRotates.valEnum) {
                BreakRotates.None -> {
                    currentYaw = -1f
                    currentPitch = -1f
                }
                BreakRotates.PotentialCrystal -> if(toPlace != null) {
                    val pos = toPlace!!.pos.up()
                    val rotates = pos.rotates
                    val yaw = rotates.first.toFloat()
                    val pitch = rotates.second.toFloat()

                    currentYaw = yaw
                    currentPitch = pitch
                } else {
                    currentYaw = -1f
                    currentPitch = -1f
                }
                BreakRotates.ExistsCrystal -> if(toBreak != null) {
                    val rotates = toBreak!!.rotates
                    val yaw = rotates.first.toFloat()
                    val pitch = rotates.second.toFloat()

                    currentYaw = yaw
                    currentPitch = pitch
                } else {
                    currentYaw = -1f
                    currentPitch = -1f
                }
            }

            RotationSystem.requestedYaw = currentYaw
            RotationSystem.requestedPitch = currentPitch

            /*if(toBreak != null) {
                rotate(
                    toBreak!!,
                    CrystalPlacementController.BREAK_ROTATE.valEnum
                )
            }*/
        }

        worldListener {
            if(toPlace != null) {
                renderer.handleRender(it.matrices, toPlace?.pos?.box(), pattern)
            }
        }

        entityAddListener {
            val entity = it.entity
            val id = entity.id

            removed.remove(id)

            if(idPredictState.value) {
                if(entity is EndCrystalEntity) {
                    offsetID = if(idPredictAdaptive.value) {
                        id - predictedID
                    } else {
                        0
                    }

                    if(idPredictDebug.value) {
                        val relativeID = predictedID + offsetID

                        println("ID Predict >> Original is $id, predicted from ${relativeID - idPredictMinRange.value} to ${relativeID + idPredictMaxRange.value}")
                    }
                }
            }
        }

        sendListener {
            when(
                val packet = it.packet
            ) {
                is PlayerInteractBlockC2SPacket -> {
                    if(idPredictState.value) {
                        predictedID = 0

                        for(entity in mc.world!!.entities) {
                            if(entity.id > predictedID) {
                                predictedID = entity.id
                            }
                        }

                        for(i in -idPredictMinRange.value..idPredictMaxRange.value) {
                            val id = predictedID + offsetID + i

                            mc.networkHandler!!.sendPacket(PlayerInteractEntityC2SPacket.attack(EntityID(id), mc.player!!.isSneaking))
                        }
                    }
                }

                is PlayerMoveC2SPacket -> {
                    if(packet.changesLook() && currentYaw != -1f && currentPitch != -1f) {
                        (packet as AccessorPlayerMoveC2SPacket).yaw = currentYaw
                        (packet as AccessorPlayerMoveC2SPacket).pitch = currentPitch
                        /*when(breakRotates.valEnum) {
                            BreakRotates.None -> { }
                            BreakRotates.PotentialCrystal -> if(toPlace != null) {
                                val pos = toPlace!!.pos.up()
                                val rotates = pos.rotates
                                val yaw = rotates.first.toFloat()
                                val pitch = rotates.second.toFloat()

                                (packet as AccessorPlayerMoveC2SPacket).yaw = yaw
                                (packet as AccessorPlayerMoveC2SPacket).pitch = pitch
                            }
                            BreakRotates.ExistsCrystal -> if(toBreak != null) {
                                val rotates = toBreak!!.rotates
                                val yaw = rotates.first.toFloat()
                                val pitch = rotates.second.toFloat()

                                (packet as AccessorPlayerMoveC2SPacket).yaw = yaw
                                (packet as AccessorPlayerMoveC2SPacket).pitch = pitch
                            }
                        }*/
                    }
                }
            }
        }

        receiveListener {
            when (it.packet) {
                is EntitiesDestroyS2CPacket -> {
                    for(id in it.packet.entityIds) {
                        removed.remove(id)

                        if(toPlace != null && toBreak != null && instaplaceOnRemove.value && id == toBreak!!.id) {
                            delayedTask(instaplaceDelay.value) {
                                doPlace(true)

                                toBreak = null
                            }
                        }
                    }
                }

                is EntitySpawnS2CPacket -> {
                    if(instabreakState.value) {
                        val id = it.packet.id
                        val type = it.packet.entityType
                        val vec = Vec3d(it.packet.x, it.packet.y, it.packet.z)

                        removed.remove(id)

                        if(type == EntityType.END_CRYSTAL && mc.player!!.pos distanceSq vec <= breakRange.value * breakRange.value) {
                            val entity = EntityID(id)

                            delayedTask(instabreakDelay.value) {
                                doBreak(entity)

                                handleSetDead(
                                    entity,
                                    instabreakSetDead.value,
                                    instabreakRemoveEntity.value
                                )

                                if(instabreakIgnoreEntity.value) {
                                    removed.add(id)
                                }

                                if(instaplaceOnInstabreak.value && toPlace != null) {
                                    doPlace(true)
                                }
                            }
                        }
                    }
                }
            }
        }

        breakListener {
            if(instacalcState.value && enemy != null) {
                val pos = it.pos
                val block = pos.block()
                val vec = Vec3d.of(pos)
                val distanceSq = mc.player!!.pos distanceSq vec

                if(block.safe() && distanceSq <= (placeRange.value + 1) * (placeRange.value + 1)) {
                    val prev = pos.state()
                    val state = Blocks.AIR.defaultState

                    //TODO: setBlockState is not threadsafe method
                    mc.world!!.setBlockState(pos, state)

                    calculatePlace()

                    mc.world!!.setBlockState(pos, prev)

                    if(instaplaceOnInstacalc.value) {
                        delayedTask(instaplaceDelay.value) {
                            doPlace(true)
                        }
                    }
                }
            }
        }

        /*motionPreListener {
            when(breakRotates.valEnum) {
                BreakRotates.None -> { }
                BreakRotates.PotentialCrystal -> if(toPlace != null) {
                    *//*val pos = toPlace!!.pos
                    val vec = Vec3d.ofCenter(pos, 1.0)
                    val rotates = vec.rotates*//*
                    val pos = toPlace!!.pos.up()
                    val rotates = pos.rotates
                    val yaw = rotates.first.toFloat()
                    val pitch = rotates.second.toFloat()

                    it.yaw = yaw
                    it.pitch = pitch
                }
                BreakRotates.ExistsCrystal -> if(toBreak != null) {
                    val rotates = toBreak!!.rotates
                    val yaw = rotates.first.toFloat()
                    val pitch = rotates.second.toFloat()

                    it.yaw = yaw
                    it.pitch = pitch
                }
            }
        }*/
    }

    enum class BreakTimings {
        Adaptive,
        Sequential
    }

    enum class SelfDamageLogics {
        AntiSuicide,
        Custom
    }

    enum class BreakRotates {
        None,
        PotentialCrystal,
        ExistsCrystal
    }

    class PlaceInfo(
        val pos : BlockPos,
        val antiDesync : Boolean,
        val willBeBroken : Boolean
    )
}