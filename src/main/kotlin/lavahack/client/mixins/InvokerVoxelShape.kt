package lavahack.client.mixins

import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(VoxelShape::class)
interface InvokerVoxelShape {
    @Invoker("getPointPosition")
    fun getPointPosition0(
        axis : Direction.Axis,
        index : Int
    ) : Double
}