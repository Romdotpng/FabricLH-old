package lavahack.client.mixins

import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * @author _kisman_
 * @since 9:45 of 26.06.2023
 */
@Mixin(Vec3d::class)
interface AccessorVec3d {
    @get:Accessor("x")
    @set:Accessor("x")
    var x : Double

    @get:Accessor("y")
    @set:Accessor("y")
    var y : Double

    @get:Accessor("z")
    @set:Accessor("z")
    var z : Double
}

var Vec3d.X : Double
    get() = this.x
    set(value) {
        (this as AccessorVec3d).x = value
    }

var Vec3d.Y : Double
    get() = this.x
    set(value) {
        (this as AccessorVec3d).y = value
    }

var Vec3d.Z : Double
    get() = this.z
    set(value) {
        (this as AccessorVec3d).z = value
    }