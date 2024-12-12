package lavahack.client.utils.world

import lavahack.client.utils.block
import lavahack.client.utils.highlight
import lavahack.client.utils.make
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

/**
 * @author _kisman_
 * @since 19:40 of 16.07.2023
 */

fun holeBlock(
    pos : BlockPos
) = when(pos.block()) {
    Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.BEDROCK -> true
    else -> false
}

fun safety(
    posses : List<BlockPos>
) : Hole.Safety {
    for(pos in posses) {
        val block = pos.block()

        if(block != Blocks.BEDROCK) {
            return Hole.Safety.Unsafe
        }
    }

    return Hole.Safety.Safe
}

fun hole(
    pos : BlockPos
) = if(pos.block() != Blocks.AIR) {
    null
} else {
    val offsets = Direction.Type.HORIZONTAL.stream().filter { !holeBlock(pos.offset(it)) }.toList()

    when(offsets.size) {
        0 -> if(pos.down().block() != Blocks.AIR && pos.up().block() == Blocks.AIR) {
            Hole(listOf(pos), safety(highlight(listOf(pos))), Hole.Type.Single)
        } else {
            null
        }

        1 -> {
            val inside = listOf(pos, pos.offset(offsets[0]))
            val outside = highlight(inside)

            var isNull = true

            for(pos0 in inside) {
                if(holeBlock(pos0.down())) {
                    isNull = false

                    continue
                }
            }

            if(!isNull) {
                for(pos0 in inside) {
                    val block = pos0.up().block()

                    if(block != Blocks.AIR) {
                        isNull = true

                        continue
                    }
                }
            }

            if(!isNull) {
                for(pos0 in outside) {
                    if(!holeBlock(pos0)) {
                        isNull = true

                        continue
                    }
                }
            }

            if(isNull) {
                null
            } else {
                Hole(inside, safety(outside), Hole.Type.Double)
            }
        }

        2 -> {
            val inside = listOf(pos, pos.offset(offsets[0]), pos.offset(offsets[1]), pos.offset(offsets[0]).offset(offsets[1]))
            val outside = highlight(inside)

            var isNull = true

            for (pos0 in inside) {
                if (holeBlock(pos0.down())) {
                    isNull = false

                    continue
                }
            }

            if(!isNull) {
                for(pos0 in inside) {
                    val block = pos0.up().block()

                    if(block != Blocks.AIR) {
                        isNull = true

                        continue
                    }
                }
            }

            if(!isNull) {
                for(pos0 in inside) {
                    val block = pos0.block()

                    if(block != Blocks.AIR) {
                        isNull = true

                        continue
                    }
                }
            }

            if(!isNull) {
                for(pos0 in outside) {
                    if(!holeBlock(pos0)) {
                        isNull = true

                        continue
                    }
                }
            }

            if(isNull) {
                null
            } else {
                Hole(inside, safety(outside), Hole.Type.Quad)
            }
        }

        else -> null
    }
}

class Hole(
    val posses : List<BlockPos>,
    val safety : Safety,
    val type : Type
) {
    val box = make(*posses.toTypedArray())

    override fun hashCode() = box.hashCode()

    enum class Safety {
        Safe,
        Unsafe
    }

    enum class Type {
        Single,
        Double,
        Quad
    }
}