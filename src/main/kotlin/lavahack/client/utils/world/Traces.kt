package lavahack.client.utils.world

import lavahack.client.utils.*
import net.minecraft.block.Blocks
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.round

fun traceDown() : TraceResult {
    var blocks = 0
    var hole = false

    var y = round(mc.player!!.y) - 1

    while(y >= 0) {
        val start = mc.player!!.pos
        val end = Vec3d(mc.player!!.x, y, mc.player!!.x)
        val result = raycast(start, end)
        val pos = BlockPos.ofFloored(end.x, end.y, end.z)

        hole = hole(pos) != null

        if(result != null && result.type == HitResult.Type.BLOCK) {
            return TraceResult(blocks, hole)
        }

        blocks++
        y--
    }

    return TraceResult(blocks, hole)
}

fun trace() : Boolean {
    val box = mc.player!!.boundingBox
    val center = box.center

    val starts = mutableListOf(
        center,
        Vec3d(box.minX, center.y, box.minZ),
        Vec3d(box.minX, center.y, box.maxZ),
        Vec3d(box.maxX, center.y, box.minZ),
        Vec3d(box.maxX, center.y, box.maxZ)
    )

    for(start in starts) {
        val end = start.add(0.0, -1.0, 0.0)

        if(traceable(start, end)) {
            return false
        }
    }

    val pos = mc.player!!.blockPos.down()
    val block = pos.block()

    return block == Blocks.AIR
}

class TraceResult(
    val blocks : Int,
    val hole : Boolean
)