package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.PlayerEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.enums.Rotates
import lavahack.client.utils.client.enums.Swaps
import lavahack.client.utils.client.interfaces.impl.motionPostListener
import lavahack.client.utils.client.interfaces.impl.motionPreListener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.mc
import lavahack.client.utils.rotates
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentHashMap

/**
 * @author _kisman_
 * @since 12:06 of 09.07.2023
 */
object BlockPlacementController : SubSystem(
    "Block Placement Controller"
) {
    val AIRPLACE = register(Setting("Airplace", true))
    val STRICT_DIRECTION = register(Setting("Strict Direction", false)/*.link(EnvironmentManager.STRICT_DIRECTION_LINKER)*/)
    val ROTATE = register(SettingEnum("Rotate", Rotates.None)/*.link(EnvironmentManager.BLOCK_ROTATE_LINKER)*/)
    val REACH_DISTANCE = register(SettingNumber("Reach Distance", 4.5, 1.0..6.0))
    private val SWAP = register(SettingEnum("Swap", Swaps.Silent))
    private val BPT = register(SettingNumber("BPT", 8, 1..30, "Blocks/Tick"))
    val HELPING_BLOCKS = register(Setting("Helping Blocks", false))

    //TODO: inventory swaps support
    //TODO: rewrite structure of this map
    //TODO: dependencies
    //TODO: exact direction
    //TODO: bps should be 1 for normal rots
    //TODO: custom rotation mode for action
    //structure: map<slot, map<base, list<triple<action, pos, list<dependency>>>>>
    private val actions = ConcurrentHashMap<Int, ConcurrentHashMap<BlockPos, MutableList<Triple<() -> Unit, BlockPos, List<BlockPos>>>>>()

    override fun init() {
        fun handleActions(
            event : PlayerEvent.Motion.Pre?
        ) {
            val completed = mutableListOf<BlockPos>()
            val prevSlot = mc.player!!.inventory.selectedSlot
            var first = true
            var swapped = false

            fun executeActions() {
                for(slot in actions.keys) {
                    val bases = actions[slot]!!.toMutableMap()

                    swapped = false

                    for(base in bases.keys) {
                        val actions = bases[base]!!

                        var fail1 = false

                        if(ROTATE.valEnum != Rotates.None) {
                            val rotates = base.rotates
                            val yaw = rotates.first.toFloat()
                            val pitch = rotates.second.toFloat()
                            var fail2 = true

                            when(ROTATE.valEnum) {
                                Rotates.None -> { }
                                Rotates.Packet -> if(event == null) {
                                    mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player!!.isOnGround))

                                    fail2 = false
                                }

                                Rotates.Normal -> if(event != null) {
                                    if(!event.spoofing) {
                                        event.yaw = yaw
                                        event.pitch = pitch
                                    }
                                } else {
                                    fail1 = true
                                    fail2 = false
                                }
                            }

                            if(fail2) {
                                /*for((action, _, _) in actions) {
                                    completed
                                }*/

                                return
                            }
                        }

                        for(triple in actions.toList()) {
                            val action = triple.first
                            val pos = triple.second
                            val dependencies = triple.third

                            if(dependencies.stream().allMatch { completed.contains(it) }) {
                                if(first) {
                                    mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player!!, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY))

                                    first = false
                                }

                                if(!swapped) {
                                    SWAP.valEnum.pre(slot)

                                    swapped = true
                                }

                                action()
                                completed.add(pos)
                                actions.remove(triple)

                                if(completed.size >= BPT.value) {
                                    return
                                }
                            }
                        }

                        if(fail1) {
                            return
                        }
                    }
                }
            }

            executeActions()

            if(!first) {
                mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player!!, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY))
            }
            if(swapped) {
                SWAP.valEnum.pre(prevSlot)
            }
        }

        motionPreListener {
            val spoofing = it.spoofing

            it.spoofing = false

            handleActions(it)

            it.spoofing = spoofing || it.spoofing
        }

        motionPostListener {
            handleActions(null)

            actions.clear()
        }
    }

    @Synchronized
    fun place(
        base : BlockPos,
        slot : Int,
        pos : BlockPos,
        dependencies : List<BlockPos> = emptyList(),
        action : () -> Unit
    ) {
        val triple = Triple(action, pos, dependencies)

        if(actions.contains(slot)) {
            val bases = actions[slot]!!

            if(bases.contains(base)) {
                bases[base]!!.add(triple)
            } else {
                bases[base] = mutableListOf(triple)
            }
        } else {
            actions[slot] = ConcurrentHashMap(mapOf(base to mutableListOf(triple)))
        }
    }
}