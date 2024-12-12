package lavahack.client.mixins

import net.minecraft.block.ShapeContext
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.RaycastContext.ShapeType
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 13:47 of 21.06.2023
 */
@Mixin(RaycastContext::class)
interface AccessorRaycastContext {
    @get:Accessor("start")
    @set:Accessor("start")
    var start : Vec3d

    @get:Accessor("end")
    @set:Accessor("end")
    var end : Vec3d

    @get:Accessor("shapeType")
    @set:Accessor("shapeType")
    var type : ShapeType

    @get:Accessor("fluid")
    @set:Accessor("fluid")
    var fluid : FluidHandling

    @get:Accessor("entityPosition")
    @set:Accessor("entityPosition")
    var pos : ShapeContext
}