package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.interfaces.IEnemyFinder
import lavahack.client.utils.client.interfaces.impl.DummyEnemyFinder
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.distanceSq
import lavahack.client.utils.friend
import lavahack.client.utils.mc
import net.minecraft.entity.player.PlayerEntity
import kotlin.reflect.KClass

/**
 * @author _kisman_
 * @since 3:54 of 08.05.2023
 */
object EnemyManager : SubSystem(
    "Enemy Manager"
) {
    val NEAREST_ENEMY_FINDER = object : IEnemyFinder {
        override var enemy : PlayerEntity? = null
        private var minDistance = Double.MAX_VALUE

        override fun process(
            player : PlayerEntity
        ) {
            val distance = mc.player!! distanceSq player

            if(distance < minDistance && !player.friend()) {
                enemy = player
                minDistance = distance
            }
        }

        override fun reset() {
            minDistance = Double.MAX_VALUE
        }
    }

    val finders = mutableListOf({ true } to NEAREST_ENEMY_FINDER)

    internal val nearestPlayer get() = NEAREST_ENEMY_FINDER.enemy
    internal val minDistance get() = if(nearestPlayer != null) mc.player!! distanceSq nearestPlayer!! else 0.0

    override fun init() {
        listener<Render3DEvent.WorldRenderer.Render.Start> {
            for((state, finder) in finders) {
                if(state()) {
                    finder.reset()
                }
            }
        }

        listener<Render3DEvent.WorldRenderer.RenderEntity.Pre> {
            val entity = it.entity

            if(entity is PlayerEntity && entity != mc.player && !entity.friend() && !entity.isDead) {
                for((state, finder) in finders) {
                    if(state()) {
                        finder.process(entity)
                    }
                }
            }
        }
    }
}

val nearest : PlayerEntity?
    get() = EnemyManager.nearestPlayer

val distanceToNearest
    get() = EnemyManager.minDistance

annotation class Targetable(
    val nearest : Boolean = false,
    val self : Boolean = false,
    val exists : Boolean = true,
    val finder : KClass<out IEnemyFinder> = DummyEnemyFinder::class
)

