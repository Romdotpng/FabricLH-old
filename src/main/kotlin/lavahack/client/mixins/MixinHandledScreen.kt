package lavahack.client.mixins

import lavahack.client.features.module.modules.render.InventoryInfo
import lavahack.client.utils.render.screen.rectWH
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.inventory.Inventories
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.awt.Color

@Mixin(HandledScreen::class)
abstract class MixinHandledScreen<T : ScreenHandler> : Screen(Text.of("")) {
    @Shadow
    private val handler : T? = null

    @Shadow
    private fun drawItem(
        context : DrawContext,
        stack : ItemStack,
        x : Int,
        y : Int,
        amountText : String
    ) { }

    @Inject(
        method = ["render"],
        at = [At("TAIL")]
    )
    private fun renderTailHook(
        context : DrawContext,
        mouseX : Int,
        mouseY : Int,
        delta : Float,
        ci : CallbackInfo
    ) {
        if(InventoryInfo.state) {
            val shulkers = mutableListOf<MutableMap<Item, Int>>()

            for(slot in handler!!.slots) {
                val stack = slot.stack
                val item = stack.item

                if(item is BlockItem) {
                    val block = item.block

                    if(block is ShulkerBoxBlock) {
                        val nbt = stack.getSubNbt("BlockEntityTag")

                        if(nbt != null) {
                            val shulkerStacks = DefaultedList.ofSize(27, ItemStack.EMPTY)
                            val shulkerItems = mutableMapOf<Item, Int>()

                            Inventories.readNbt(nbt, shulkerStacks)

                            shulkers.add(shulkerItems)

                            for(shulkerStack in shulkerStacks) {
                                val shulkerItem = shulkerStack.item
                                val shulkerCount = shulkerStack.count

                                shulkerItems[shulkerItem] = (shulkerItems[shulkerItem] ?: 0) + shulkerCount
                            }
                        }
                    }
                }
            }

            val x = 5
            var y = 5

            for(items in shulkers) {
                val width = items.size.coerceIn(0..9) * 16
                val height = (items.size / 9 + 1) * 16 + 5

                rectWH(
                    context,
                    x,
                    y,
                    width,
                    height,
                    Color(0, 0, 0, 120)
                )

                for((j, entry) in items.entries.withIndex()) {
                    val item = entry.key
                    val count = entry.value
                    val itemX = j % 9 * 16 + x
                    val itemY = j / 9 * 16 + y

                    drawItem(
                        context,
                        ItemStack(item),
                        itemX,
                        itemY,
                        count.toString()
                    )
                }

                y += height
            }
        }
    }
}