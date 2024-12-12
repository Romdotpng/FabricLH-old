package lavahack.client.utils.client.interfaces

import net.minecraft.entity.player.PlayerEntity

interface IEnemyFinder {
    var enemy : PlayerEntity?

    fun process(
        player : PlayerEntity
    )

    fun reset()
}