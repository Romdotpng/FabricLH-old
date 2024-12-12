package lavahack.client.utils.entity

import lavahack.client.utils.mc
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.nbt.NbtCompound

/**
 * @author _kisman_
 * @since 22:49 of 03.08.2023
 */
class EntityID(
    id : Int
) : Entity(EntityType.ENDER_PEARL, mc.world!!) {
    init {
        this.id = id
    }

    override fun initDataTracker() { }

    override fun readCustomDataFromNbt(
        nbt : NbtCompound
    ) { }

    override fun writeCustomDataToNbt(
        nbt : NbtCompound
    ) { }
}