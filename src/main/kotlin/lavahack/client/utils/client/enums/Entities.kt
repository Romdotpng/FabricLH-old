package lavahack.client.utils.client.enums

import net.minecraft.entity.Entity
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import kotlin.reflect.KClass

enum class Entities(
    val filter : (Entity) -> Boolean
) {
    Players({ it is PlayerEntity }),
    Animals({ it is AnimalEntity }),
    Mobs({ it is MobEntity }),
    Crystals({ it is EndCrystalEntity }),
    Orbs({ it is ExperienceOrbEntity }),
    Boats({ it is BoatEntity })
}