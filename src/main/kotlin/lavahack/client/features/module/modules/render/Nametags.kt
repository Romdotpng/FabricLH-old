package lavahack.client.features.module.modules.render

import lavahack.client.event.events.Render3DEvent
import lavahack.client.features.module.Module
import lavahack.client.features.subsystem.subsystems.PopListener
import lavahack.client.features.subsystem.subsystems.formatted
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.*
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.render.screen.*
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting
import java.awt.Color

/**
 * @author _kisman_
 * @since 22:37 of 28.05.2023
 */
@Module.Info(
    name = "Nametags",
    description = "Shows info about players above their heads",
    category = Module.Category.RENDER
)
class Nametags : Module() {
    init {
        val players = register(Setting("Players", true))
        val items = register(Setting("Items", false))
        val textColor = register(Setting("Text Color", Color(255, 255, 255, 255)))
        val background = register(Setting("Background", false))
        val backgroundColor = register(Setting("Background Color", Color(0, 0, 0, 120)))
        val scaleValue = register(SettingNumber("Scale", 1f, 0.1f..10f))
        val scaleFactor = register(SettingNumber("Scale Factor", 1f, 0.5f..10f))
        val minScale = register(SettingNumber("Min Scale", 1f, 0.1f..10f))
        val maxScale = register(SettingNumber("Max Scale", 1f, 0.1f..10f))
        val showHealth = register(Setting("Show Health", true))
        val showPing = register(Setting("Show Ping", true))
        val showPops = register(Setting("Show Pops", false))
        val friendHighlight = register(Setting("Friend Highlight", true))
        val enemyHighlight = register(Setting("Enemy Highlight", true))

        fun text(
            entity : Entity
        ) = formatted(
            when(entity) {
                is PlayerEntity -> {
                    val elements = mutableListOf<String>()

                    if(showPing.value) {
                        elements.add("${entity.ping()}ms")
                    }

                    elements.add(entity.name.string)

                    if(showHealth.value) {
                        elements.add("${(entity.health + entity.absorptionAmount).toInt()}")
                    }

                    if(showPops.value) {
                        elements.add("${PopListener.pops[entity] ?: 0} pops")
                    }

                    elements.joinToString(" ")
                }

                is ItemEntity -> entity.stack.let { "${I18n.translate(it.name.string)} x${it.count}" }
                else -> "INVALID ENTITY TYPE"
            },
            if(entity is PlayerEntity) {
                if(entity.friend() && friendHighlight.value) {
                    Formatting.AQUA
                } else if(entity.enemy() && enemyHighlight.value) {
                    Formatting.RED
                } else {
                    Formatting.WHITE
                }
            } else {
                Formatting.WHITE
            }
        )

        listener<Render3DEvent.RenderEntity.Post> {
            if(it.entity != mc.player && ((players.value && it.entity is PlayerEntity) || (items.value && it.entity is ItemEntity))) {
                val text = text(it.entity)
                val rotation = mc.entityRenderDispatcher.rotation
                val offset = mc.entityRenderDispatcher.getRenderer(it.entity).getPositionOffset(it.entity, it.delta)
                val x = it.x + offset.x
                val y = it.y + offset.y
                val z = it.z + offset.z
                val height = it.entity.nameLabelHeight
                val scale = 0.025f * (scaleValue.value * (mc.player!! distanceSq it.entity).toFloat() / scaleFactor.value).coerceIn(minScale.value..maxScale.value)

                it.matrices.push()
                it.matrices.translate(x, y, z)
                it.matrices.push()
                it.matrices.translate(0f, height, 0f)
                it.matrices.multiply(rotation)
                it.matrices.scale(-scale, -scale, scale)

                if(background.value) {
                    rectWH(
                        it.matrices.context(),
                        -stringWidth(text.string) / 2.0 - 1,
                        -1,
                        stringWidth(text.string) + 2,
                        fontHeight() + 1,
                        backgroundColor.value
                    )
                }

                drawString(
                    it.matrices.context(),
                    text,
                    -stringWidth(text.string) / 2.0,
                    0,
                    textColor.value,
                    true,
                    TextRenderer.TextLayerType.SEE_THROUGH
                )

                drawString(
                    it.matrices.context(),
                    text,
                    -stringWidth(text.string) / 2.0,
                    0,
                    textColor.value,
                    false,
                    TextRenderer.TextLayerType.SEE_THROUGH
                )

                it.matrices.pop()
                it.matrices.pop()
            }
        }
    }
}