package lavahack.client.settings.pattern.patterns

import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.Pattern
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.ICallbackRegistry
import lavahack.client.utils.client.interfaces.IListenerRegistry
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.render.world.SlideRenderer
import lavahack.client.utils.threads.delayedTask
import net.minecraft.block.Blocks
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentLinkedQueue

@Suppress("PrivatePropertyName")
class BlockPlacerPattern(
    listeners : IListenerRegistry? = null,
    callbacks : ICallbackRegistry? = null,
    private val useCrystalBreaker : Boolean = false,
//    private val useCrystalInstabreaker : Boolean = false,
    private val onPlace : (BlockPos) -> Unit = { }
) : Pattern() {
    private val RENDER_GROUP = register(SettingGroup("Render"))
    private val PATTERN = register(RENDER_GROUP.add(SlideRenderingPattern()))
    private val PACKET = register(Setting("Packet", true))
    private val SWING = register(Setting("Swing", true))
    //TODO: move helping blocks to block placement pattern?

    //TODO: move crystal breaker to separate pattern
    private val CRYSTAL_BREAKER_GROUP = register(SettingGroup("Crystal Breaker").visible { useCrystalBreaker })
    private val CRYSTAL_BREAKER_STATE = register(CRYSTAL_BREAKER_GROUP.add(Setting("State", false)))
    private val CRYSTAL_BREAKER_SWING = register(CRYSTAL_BREAKER_GROUP.add(Setting("Swing", true)))
    private val CRYSTAL_BREAKER_DELAY = register(CRYSTAL_BREAKER_GROUP.add(SettingNumber("Delay", 0L, 0L..1000L)))
    private val CRYSTAL_BREAKER_SET_DEAD = register(CRYSTAL_BREAKER_GROUP.add(Setting("Set Dead", false)))
    private val CRYSTAL_BREAKER_REMOVE_ENTITY = register(CRYSTAL_BREAKER_GROUP.add(Setting("Remove Entity", false)))

    //TODO: make obby placer pattern with crystal instabreaker
    /*private val CRYSTAL_INSTABREAKER_GROUP = register(SettingGroup("Crystal Instabreaker").visible { useCrystalInstabreaker })
    private val CRYSTAL_INSTABREAKER_STATE = register(CRYSTAL_BREAKER_GROUP.add(Setting("State", false)))
    private val CRYSTAL_INSTABREAKER_DELAY = register(CRYSTAL_BREAKER_GROUP.add(SettingNumber("Delay", 0L, 0L..100L)))
    private val CRYSTAL_INSTABREAKER_SET_DEAD = register(CRYSTAL_BREAKER_GROUP.add(Setting("Set Dead", false)))
    private val CRYSTAL_INSTABREAKER_REMOVE_ENTITY = register(CRYSTAL_BREAKER_GROUP.add(Setting("Remove Entity", false)))*/

    private val RENDERER = SlideRenderer()

    private val queue = ConcurrentLinkedQueue<BlockPos>()

    init {
        RENDER_GROUP.prefix("Render")
        CRYSTAL_BREAKER_GROUP.prefix("Crystal Breaker")
//        CRYSTAL_INSTABREAKER_GROUP.prefix("Crystal Instabreaker")

        callbacks?.enableCallback {
            reset()
        }

        listeners?.worldListener {
            render(it.matrices)
        }
    }

    private fun handleCrystalBreaker(
        posses : Collection<BlockPos>
    ) {
        if(CRYSTAL_BREAKER_STATE.value) {
            val broken = mutableSetOf<Entity>()

            for(pos in posses) {
                val block = pos.block()
                var entity : Entity? = null

                if(block == Blocks.AIR && intersects(pos.box()) { it0 -> (it0 is EndCrystalEntity).also { it1 -> if(it1) entity = it0 } }) {
                    if(!broken.contains(entity!!)) {
                        delayedTask(CRYSTAL_BREAKER_DELAY.value) {
                            leftClickEntity(
                                entity!!,
                                swing = CRYSTAL_BREAKER_SWING.value
                            )

                            handleSetDead(
                                entity!!,
                                CRYSTAL_BREAKER_SET_DEAD.value,
                                CRYSTAL_BREAKER_REMOVE_ENTITY.value
                            )
                        }
                    }

                    broken.add(entity!!)
                }
            }
        }
    }

    fun place(
        pos : BlockPos,
        item : Item,
        placed : MutableCollection<BlockPos> = mutableListOf(),
        success : () -> Unit = { }
    ) {
        place(listOf(pos), item, placed, success)
    }

    //TODO: rewrite logic of success thing
    fun place(
        posses : Collection<BlockPos>,
        item : Item,
        placed : MutableCollection<BlockPos> = mutableListOf(),
        success : () -> Unit = { }
    ) {
        if(posses.isNotEmpty()) {
            val slot = InventoryLocations.Hotbar.findInventoryItem(item)

            if(slot != -1) {
                handleCrystalBreaker(posses)

                for(pos in posses) {
                    if(placeable(pos)) {
                        queue.add(pos)
                        placeBlock(pos, item, packet = PACKET.value, swing = SWING.value, placed = placed)
                        placed.add(pos)
                    }
                }

                success()
            }
        }
    }

    fun reset() {
        RENDERER.reset()
        queue.clear()
    }

    fun render(
        matrices : MatrixStack
    ) {
        while(queue.isNotEmpty()) {
            val pos = queue.poll()

            RENDERER.handleRender(matrices, pos.box(), PATTERN)
        }

        RENDERER.handleRender(matrices, null, PATTERN)
    }
}