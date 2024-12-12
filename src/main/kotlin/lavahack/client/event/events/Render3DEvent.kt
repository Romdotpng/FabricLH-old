package lavahack.client.event.events

import lavahack.client.event.bus.Event
import lavahack.client.utils.mc
import net.minecraft.client.model.Model
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity

/**
 * @author _kisman_
 * @since 11:42 of 26.05.2023
 */
open class Render3DEvent : Event() {
    class Pre(val matrices : MatrixStack, val delta : Float) : Render3DEvent()
    class Post(val matrices : MatrixStack, val delta : Float) : Render3DEvent()
    class DefaultBlockOutline : Render3DEvent()
    class WorldBorder : Render3DEvent()
    class Weather : Render3DEvent()
    class EntityFire : Render3DEvent()
    class EntityNametag(val entity : Entity) : Render3DEvent()
    class TiltView() : Render3DEvent()

    class Overlay {
        class Underwater : Render3DEvent()
        class Fire : Render3DEvent()
    }

    class RenderEntity {
        class Pre(
            val entity : Entity,
            val matrices : MatrixStack,
            val delta : Float,
            val x : Double,
            val y : Double,
            val z : Double,
            val yaw : Float,
            val consumers : VertexConsumerProvider,
            val renderer : net.minecraft.client.render.entity.EntityRenderer<in Entity> = mc.entityRenderDispatcher.getRenderer(entity)
        ) : Render3DEvent()

        class Post(
            val entity : Entity,
            val matrices : MatrixStack,
            val delta : Float,
            val x : Double,
            val y : Double,
            val z : Double,
            val yaw : Float,
            val consumers : VertexConsumerProvider,
            val renderer : net.minecraft.client.render.entity.EntityRenderer<in Entity> = mc.entityRenderDispatcher.getRenderer(entity)
        ) : Render3DEvent()
    }


    class WorldRenderer {
        class Render {
            class Start(val matrices : MatrixStack) : Render3DEvent()
            class End : Render3DEvent()

            class Entity {
                class Pre(
                    val entity : net.minecraft.entity.Entity,
                    val matrices : MatrixStack,
                    val consumers : VertexConsumerProvider,
                    val cameraX : Double,
                    val cameraY : Double,
                    val cameraZ : Double,
                    val delta : Float
                ) : Render3DEvent()

                class Post(
                    val entity : net.minecraft.entity.Entity,
                    val matrices : MatrixStack,
                    val consumers : VertexConsumerProvider,
                    val cameraX : Double,
                    val cameraY : Double,
                    val cameraZ : Double,
                    val delta : Float
                ) : Render3DEvent()
            }
        }

        class RenderEntity {
            class Pre(
                val entity : Entity,
                val matrices : MatrixStack,
                val consumers : VertexConsumerProvider,
                val cameraX : Double,
                val cameraY : Double,
                val cameraZ : Double,
                val delta : Float,
            ) : Render3DEvent()

            class Post(val entity : Entity) : Render3DEvent()
        }
    }

    class EntityRenderer {
        class RenderModel(
            val entity : Entity,
            val model : Model,
            val matrices : MatrixStack,
            val consumer : VertexConsumer,
            val light : Int,
            val overlay : Int,
            val red : Float,
            val green : Float,
            val blue : Float,
            val alpha : Float
        ) : Render3DEvent()

        class Render {
            class Pre(val entity : Entity) : Render3DEvent()
            class Post(val entity : Entity) : Render3DEvent()
        }
    }
}