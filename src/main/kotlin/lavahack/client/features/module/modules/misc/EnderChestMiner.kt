package lavahack.client.features.module.modules.misc

import lavahack.client.features.module.Module
import lavahack.client.features.module.enableCallback
import lavahack.client.settings.Setting
import lavahack.client.settings.pattern.patterns.BlockMinerPattern
import lavahack.client.settings.pattern.patterns.BlockPlacerPattern
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.chat.ChatUtility
import lavahack.client.utils.client.enums.InventoryLocations
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import java.util.Comparator

@Module.Info(
    name = "EnderChestMiner",
    description = "Automatically mines ender chests",
    category = Module.Category.MISC
)
class EnderChestMiner : Module() {
    init {
        val enderChestCount = register(SettingNumber("Ender Chest Count", 0, 0..64))
        val autoDisable = register(Setting("Auto Disable", true))
        val notify = register(Setting("Notify", true))
        val placer = register(BlockPlacerPattern(this, null))
        val miner = register(BlockMinerPattern())

        var pos : BlockPos? = null
        var counter = 0

        fun reset() {
            pos = null
            counter = 0
        }

        enableCallback {
            reset()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            if(enderChestCount.value in 1 until counter) {
                state = false

                return@tickListener
            }

            val slot = InventoryLocations.Hotbar.findInventoryItem(Items.ENDER_CHEST)

            if(slot != -1) {
                if(pos == null) {
                    val sphere = sphere(mc.player!!.blockPos, 2)

                    sphere.sortWith(Comparator.comparingDouble { mc.player!!.pos distanceSq it.vec() })

                    val first = sphere.firstOrNull { placeable(it) && it.y >= mc.player!!.blockPos.y }

                    pos = first
                }

                if(pos != null) {
                    val block = pos!!.block()

                    if(block == Blocks.ENDER_CHEST) {
                        miner.mine(pos!!)
                    } else if(block == Blocks.AIR) {
                        placer.place(pos!!, Items.ENDER_CHEST) {
                            miner.reset()
                            counter++
                        }
                    }
                } else {
                    if(autoDisable.value) {
                        if(notify.value) {
                            ChatUtility.INFO.print("Not have valid position! Disabling!")
                        }

                        state = false
                    }
                }
            } else {
                if(autoDisable.value) {
                    if(notify.value) {
                        ChatUtility.INFO.print("Not enough ender chests! Disabling!")
                    }

                    state = false
                } else {
                    reset()
                }
            }
        }
    }
}