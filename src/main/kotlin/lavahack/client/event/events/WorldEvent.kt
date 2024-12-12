package lavahack.client.event.events

import lavahack.client.event.bus.Event
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 11:03 of 06.07.2023
 */
open class WorldEvent : Event() {
    class Add(val entity : Entity) : WorldEvent()
    class Remove(val entity : Entity, val reason : Entity.RemovalReason) : WorldEvent()

    class BlockUpdate {
        class Place(val pos : BlockPos, val state : BlockState) : WorldEvent()
        class Break(val pos : BlockPos) : WorldEvent()
    }

    class Pop(val entity : Entity) : WorldEvent()
    class Death(val entity : PlayerEntity) : WorldEvent()
}