@file:Suppress("LocalVariableName", "UNNECESSARY_NOT_NULL_ASSERTION")

package lavahack.client.utils

import com.google.common.collect.ImmutableMap
import io.netty.buffer.Unpooled
import lavahack.client.LavaHack
import lavahack.client.event.events.ScreenEvent
import lavahack.client.features.friend.Friends
import lavahack.client.features.subsystem.subsystems.*
import lavahack.client.mixins.*
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.combo.Element
import lavahack.client.utils.client.enums.*
import lavahack.client.utils.client.interfaces.IVisible
import lavahack.client.utils.client.interfaces.mixins.IClientConnection
import lavahack.client.utils.math.*
import lavahack.client.utils.math.atan2
import lavahack.client.utils.math.cos
import lavahack.client.utils.math.sin
import lavahack.client.utils.minecraft.LavaHackShaderProgram
import lavahack.client.utils.render.shader.Shader
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.*
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.*
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.Difficulty
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import net.minecraft.world.entity.SimpleEntityLookup
import org.apache.commons.lang3.StringUtils
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.*

/**
 * @author _kisman_
 * @since 10:21 of 08.05.2023
 */

val mc = MinecraftClient.getInstance()!!
val tessellator = Tessellator.getInstance()!!
val buffer = tessellator.buffer!!

const val SPRINTING_SPEED = 0.2873
const val WALKING_SPEED = 0.221

const val EXPLOSION_SIZE = 12.0

const val INT_MAX_POWER_OF_TWO = 1 shl (Int.SIZE_BITS - 2)

val formats = mapOf(
    "position" to ("Position" to VertexFormats.POSITION_ELEMENT!!),
    "color" to ("Color" to VertexFormats.COLOR_ELEMENT!!),
    "texture" to ("UV0" to VertexFormats.TEXTURE_ELEMENT!!),
    "overlay" to ("UV1" to VertexFormats.OVERLAY_ELEMENT!!),
    "light" to ("UV2" to VertexFormats.LIGHT_ELEMENT!!),
    "normal" to ("Normal" to VertexFormats.NORMAL_ELEMENT!!),
    "padding" to ("Padding" to VertexFormats.PADDING_ELEMENT!!),
    "uv" to ("UV" to VertexFormats.UV_ELEMENT!!)
)

val armorSlots = mapOf(
    ArmorItem.Type.HELMET to 5,
    ArmorItem.Type.CHESTPLATE to 6,
    ArmorItem.Type.LEGGINGS to 7,
    ArmorItem.Type.BOOTS to 8
)

val safeBlocks = listOf(
    Blocks.OBSIDIAN,
    Blocks.BEDROCK,
    Blocks.CRYING_OBSIDIAN,
    Blocks.NETHERITE_BLOCK
)

fun keyName(
    key : Int
) = when (key) {
    GLFW_KEY_UNKNOWN -> "Unknown"
    GLFW_KEY_ESCAPE -> "Esc"
    GLFW_KEY_GRAVE_ACCENT -> "Grave Accent"
    GLFW_KEY_WORLD_1 -> "World 1"
    GLFW_KEY_WORLD_2 -> "World 2"
    GLFW_KEY_PRINT_SCREEN -> "Print Screen"
    GLFW_KEY_PAUSE -> "Pause"
    GLFW_KEY_INSERT -> "Insert"
    GLFW_KEY_DELETE -> "Delete"
    GLFW_KEY_HOME -> "Home"
    GLFW_KEY_PAGE_UP -> "Page Up"
    GLFW_KEY_PAGE_DOWN -> "Page Down"
    GLFW_KEY_END -> "End"
    GLFW_KEY_TAB -> "Tab"
    GLFW_KEY_LEFT_CONTROL -> "Left Control"
    GLFW_KEY_RIGHT_CONTROL -> "Right Control"
    GLFW_KEY_LEFT_ALT -> "Left Alt"
    GLFW_KEY_RIGHT_ALT -> "Right Alt"
    GLFW_KEY_LEFT_SHIFT -> "Left Shift"
    GLFW_KEY_RIGHT_SHIFT -> "Right Shift"
    GLFW_KEY_UP -> "Arrow Up"
    GLFW_KEY_DOWN -> "Arrow Down"
    GLFW_KEY_LEFT -> "Arrow Left"
    GLFW_KEY_RIGHT -> "Arrow Right"
    GLFW_KEY_APOSTROPHE -> "Apostrophe"
    GLFW_KEY_BACKSPACE -> "Backspace"
    GLFW_KEY_CAPS_LOCK -> "Caps Lock"
    GLFW_KEY_MENU -> "Menu"
    GLFW_KEY_LEFT_SUPER -> "Left Super"
    GLFW_KEY_RIGHT_SUPER -> "Right Super"
    GLFW_KEY_ENTER -> "Enter"
    GLFW_KEY_KP_ENTER -> "Numpad Enter"
    GLFW_KEY_NUM_LOCK -> "Num Lock"
    GLFW_KEY_SPACE -> "Space"
    GLFW_KEY_F1 -> "F1"
    GLFW_KEY_F2 -> "F2"
    GLFW_KEY_F3 -> "F3"
    GLFW_KEY_F4 -> "F4"
    GLFW_KEY_F5 -> "F5"
    GLFW_KEY_F6 -> "F6"
    GLFW_KEY_F7 -> "F7"
    GLFW_KEY_F8 -> "F8"
    GLFW_KEY_F9 -> "F9"
    GLFW_KEY_F10 -> "F10"
    GLFW_KEY_F11 -> "F11"
    GLFW_KEY_F12 -> "F12"
    GLFW_KEY_F13 -> "F13"
    GLFW_KEY_F14 -> "F14"
    GLFW_KEY_F15 -> "F15"
    GLFW_KEY_F16 -> "F16"
    GLFW_KEY_F17 -> "F17"
    GLFW_KEY_F18 -> "F18"
    GLFW_KEY_F19 -> "F19"
    GLFW_KEY_F20 -> "F20"
    GLFW_KEY_F21 -> "F21"
    GLFW_KEY_F22 -> "F22"
    GLFW_KEY_F23 -> "F23"
    GLFW_KEY_F24 -> "F24"
    GLFW_KEY_F25 -> "F25"
    else -> {
        val name = glfwGetKeyName(key, 0)

        if(name == null) {
            "Unknown"
        } else {
            StringUtils.capitalize(name)
        }
    }
}!!

fun buttonName(
    button : Int
) = when (button) {
    -1 -> "Unknown"
    0 -> "Mouse Left"
    1 -> "Mouse Right"
    2 -> "Mouse Middle"
    else -> "Mouse $button"
}

fun keyAction(
    action : Int
) = when(action) {
    GLFW_PRESS -> KeyActions.Press
    GLFW_RELEASE -> KeyActions.Release
    else -> KeyActions.Repeat
}

fun compare(
    lambda1 : () -> Unit,
    lambda2 : () -> Unit
) : () -> Unit = {
    lambda1()
    lambda2()
}

fun <T> compare(
    lambda1 : (T) -> Unit,
    lambda2 : (T) -> Unit
) : (T) -> Unit = {
    lambda1(it)
    lambda2(it)
}

fun Color.hue(
    hue : Number
) : Color {
    val hsb = Color.RGBtoHSB(this.red, this.green, this.blue, null)

    return Color.getHSBColor(hue.toFloat(), hsb[1], hsb[2])
}

fun Color.saturation(
    saturation : Number
) : Color {
    val hsb = Color.RGBtoHSB(this.red, this.green, this.blue, null)

    return Color.getHSBColor(hsb[0], saturation.toFloat(), hsb[2])
}

fun Color.brightness(
    brightness : Number
) : Color {
    val hsb = Color.RGBtoHSB(this.red, this.green, this.blue, null)

    return Color.getHSBColor(hsb[0], hsb[1], brightness.toFloat())
}

fun Color.toHSB(
    hsb : FloatArray? = null
) = Color.RGBtoHSB(this.red, this.green, this.blue, hsb)!!

fun addTask(
    task : () -> Unit
) {
    (mc as AccessorMinecraftClient).renderTaskQueue()?.add(Runnable { task() }) ?: task()
}

fun VoxelShape.box() = Box(
    getMin(Direction.Axis.X),
    getMin(Direction.Axis.Y),
    getMin(Direction.Axis.Z),
    getMax(Direction.Axis.X),
    getMax(Direction.Axis.Y),
    getMax(Direction.Axis.Z)
)

fun BlockPos.state() = mc.world!!.getBlockState(this)!!

fun BlockPos.block() = state().block!!

fun BlockPos.box() = (if(block() == Blocks.AIR) Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) else state().getOutlineShape(mc.world!!, this).box().run { this }).offset(this)!!

fun asString(
    value : Any
) = when(value) {
    is Color -> value.rgb
    else -> value
}.toString()

fun MatrixStack.rotate(
    x : Number,
    y : Number,
    z : Number
) {
    multiply(Quaternionf().rotate(x, y, z))
}

fun moveSpeed(
    base : Double = SPRINTING_SPEED,
    multiplier : Double = 1.0
) = if(mc.player?.hasStatusEffect(StatusEffects.SPEED) == true) {
    val effect = mc.player!!.getStatusEffect(StatusEffects.SPEED)!!
    val amplifier = effect.amplifier

    base * (1.0 + 0.2 * (amplifier + 1))
} else {
    base
} * multiplier

fun strafe(
    multiplier : Double = 1.0
) = strafe(speed = moveSpeed(multiplier = multiplier))

fun strafe(
    entity : Entity = mc.player!!,
    input : Input = mc.player!!.input,
    speed : Double
) : Array<Double> {
    val forward = sign(input.movementForward)
    var sideways = sign(input.movementSideways)
    var yaw = entity.yaw + 90

    if(forward != 0f) {
        yaw += sideways * if(forward > 0f) {
            -45
        } else {
            45
        }

        sideways = 0f
    }

    yaw *= RAD

    val motionX = forward * speed * cos(yaw) + sideways * speed * sin(yaw)
    val motionZ = forward * speed * sin(yaw) - sideways * speed * cos(yaw)

    return arrayOf(motionX, motionZ)
}

fun move(
    current : Vector2d,
    target : Vector2d,
    speed : Number
) : Pair<Double, Double> {
    //TODO: why not target - current?
    val diff = current.sub(target)
//    val diff = target.sub(current)
    val diffX = diff.x
    val diffZ = diff.y
    val yaw = atan2(diffX, diffZ) // it has [0; 360] format, like after using Utility::wrap
    val distanceSq = current distanceSq target
    val hypot = if(distanceSq < speed * speed) sqrt(distanceSq) else speed.toDouble()

    return hypot * cos(yaw) to hypot * sin(yaw)
}

val BlockPos.xz get() = Vector2d(x.toDouble(), z.toDouble())

val Vec3d.xz get() = Vector2d(x, z)

fun findInventoryItem(
    item : Item
) = findInventoryItem { it == item }

fun findItem(
    item : Item,
    vararg locations : InventoryLocations
) = findItem(
    { it == item },
    *locations
)

fun findItem(
    item : Item,
    locations : List<InventoryLocations>
) = findItem(
    { it == item },
    locations
)

fun findInventoryItem(
    check : (Item) -> Boolean
) = findItem(
    check,
    InventoryLocations.Inventory,
    InventoryLocations.Hotbar
)

fun findItem(
    check : (Item) -> Boolean,
    vararg locations : InventoryLocations
) = findItem(
    check,
    locations.toList()
)

fun findItem(
    check : (Item) -> Boolean,
    locations : List<InventoryLocations> 
) : Int {
    for(location in locations) {
        val slot = location.findContainerItem(check)
        
        if(slot != -1) {
            return slot
        }
    }
    
    return -1
}

//TODO: fix if another container is opened
fun inventorySwap(
    to : Int
) {
    mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, to, mc.player!!.inventory.selectedSlot, SlotActionType.SWAP, mc.player!!)
}

fun inventoryHotbarSwap(
    inventory : Int,
    hotbar : Int
) {
    mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, inventory, hotbar, SlotActionType.SWAP, mc.player!!)
}

fun inventorySwap(
    from : Int,
    to : Int
) {
    val id = mc.player!!.currentScreenHandler?.syncId ?: 0

    mc.interactionManager!!.clickSlot(id, from, 0, SlotActionType.PICKUP, mc.player!!)
    mc.interactionManager!!.clickSlot(id, to, 0, SlotActionType.PICKUP, mc.player!!)
    mc.interactionManager!!.clickSlot(id, from, 0, SlotActionType.PICKUP, mc.player!!)
}

fun programOf(
    name : String?,
    elements : Map<String, VertexFormatElement>,
    shader : Shader? = null
) = if(name != null) {
    val format = VertexFormat(ImmutableMap.builder<String, VertexFormatElement>().putAll(elements).build())

    if(shader == null) {
        ShaderProgram(
            LavaHack.RESOURCE_FACTORY,
            name,
            format
        )
    } else {
        LavaHackShaderProgram(
            shader!!,
            name,
            format
        )
    }
} else {
    null
}

fun Identifier.asStream() = try{
    javaClass.getResourceAsStream("/assets/$namespace/$path")!!
} catch(exception : Exception) {
    println("/assets/$namespace/$path")

    throw exception
}

infix fun Vec3d.distanceSq(
    to : Vec3d
) : Double {
    val diffX = this.x - to.x
    val diffY = this.y - to.y
    val diffZ = this.z - to.z

    return diffX * diffX + diffY * diffY + diffZ * diffZ
}

infix fun Entity.distanceSq(
    to : Entity
) = this.pos distanceSq to.pos

infix fun Vec3d.distance(
    to : Vec3d
) = sqrt(this distanceSq to)

infix fun Vector2d.distanceSq(
    to : Vector2d
) : Double {
    val diffX = this.x - to.x
    val diffY = this.y - to.y

    return diffX * diffX + diffY * diffY
}

infix fun ClosedRange<Double>.step(
    step : Double
) : Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }

    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) {
            null
        } else {
            val next = previous + step

            if (next > endInclusive) {
                null
            } else {
                next
            }
        }
    }

    return sequence.asIterable()
}

fun raycast(
    world : World,
    context : RaycastContext,
    pos : BlockPos,
    terrain : Boolean
) = BlockView.raycast(
    context.start,
    context.end,
    context,
    { _context, _pos ->
        val state = if(pos == _pos) {
            Blocks.OBSIDIAN.defaultState
        } else {
            val _state = _pos.state()

            if(_state.block.blastResistance < 600f && terrain) {
                Blocks.AIR.defaultState
            } else {
                _state
            }
        }

        val start = _context.start
        val end = _context.end

        val shape1 = _context.getBlockShape(state, world, _pos)
        val shape2 = VoxelShapes.empty()

        val result1 = world.raycastBlock(start, end, _pos, shape1, state)
        val result2 = shape2.raycast(start, end, _pos)

        val distance1 = if(result1 == null) Double.MAX_VALUE else _context.start.squaredDistanceTo(result1.pos)
        val distance2 = if(result2 == null) Double.MAX_VALUE else _context.start.squaredDistanceTo(result2.pos)

        if(distance1 <= distance2) {
            result1
        } else {
            result2
        }
    },
    { _context ->
        val facing = _context.start.subtract(_context.end)

        BlockHitResult.createMissed(_context.end, Direction.getFacing(facing.x, facing.y, facing.z), BlockPos.ofFloored(_context.end))
    }
)!!

fun exposure(
    world : World,
    source : Vec3d,
    entity : Entity,
    pos : BlockPos,
    predict : Boolean,
    terrain : Boolean
) : Double {
    val box = if(predict) {
        entity.boundingBox.offset(entity.velocity)
    } else {
        entity.boundingBox
    }

    val offsetX = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0)
    val offsetY = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0)
    val offsetZ = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0)

    val startX = (1.0 - floor(1.0 / offsetX) * offsetX) / 2.0
    val startZ = (1.0 - floor(1.0 / offsetZ) * offsetZ) / 2.0

    return if(offsetX >= 0 && offsetY >= 0 && offsetZ >= 0) {
        var i = 0.0
        var j = 0.0

        for(deltaX in 0.0..1.0 step offsetX) {
            for(deltaY in 0.0..1.0 step offsetY) {
                for(deltaZ in 0.0..1.0 step offsetZ) {
                    val x = MathHelper.lerp(deltaX, box.minX, box.maxX)
                    val y = MathHelper.lerp(deltaY, box.minY, box.maxY)
                    val z = MathHelper.lerp(deltaZ, box.minZ, box.maxZ)

                    val start = Vec3d(x + startX, y, z + startZ)
                    val context = RaycastContext(start, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)

                    if(raycast(world, context, pos, terrain).type == HitResult.Type.MISS) {
                        i++
                    }

                    j++
                }
            }
        }

        i / j
    } else {
        0.0
    }
}

fun damageForDifficulty(
    damage : Double
) = when(mc.world!!.difficulty) {
    Difficulty.PEACEFUL -> 0.0
    Difficulty.EASY -> min(damage / 2 + 1, damage)
    Difficulty.HARD -> damage * 3 / 2
    else -> damage
}

fun resistanceReduction(
    entity : LivingEntity,
    damage : Double
) = if(entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
    val effect = entity.getStatusEffect(StatusEffects.RESISTANCE)!!
    val amplifier = effect.amplifier

    max(0.0, damage * (1.0 - amplifier * 0.2))
} else {
    damage
}

fun damageByCrystal(
    world : World = mc.world!!,
    from : Vec3d,
    to : PlayerEntity,
    pos : BlockPos,
    predict : Boolean = false,
    interpolation : Int = 0,
    terrain : Boolean
) : Double {
    val box = if(predict) {
        to.boundingBox.offset(to.velocity)
    } else {
        to.boundingBox
    }

    val distance = from distanceSq box.center

    return if(distance > EXPLOSION_SIZE * EXPLOSION_SIZE) {
        0.0
    } else {
        val exposure = exposure(world, from, to, pos, predict, terrain)
        val impact = (1.0 - (distance / (EXPLOSION_SIZE * EXPLOSION_SIZE))) * exposure
        var damage = (impact * impact * impact) / 2.0 * 7.0 * (6.0 * 2.0) + 1.0

        damage = damageForDifficulty(damage)
        damage = DamageUtil.getDamageLeft(damage.toFloat(), to.armor.toFloat(), to.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)!!.value.toFloat()).toDouble()
        damage = resistanceReduction(to, damage)

        max(0.0, damage)
    }
}

fun damageByCrystal(
    from : EndCrystalEntity,
    to : PlayerEntity,
    predict : Boolean,
    terrain : Boolean
) = damageByCrystal(
    mc.world!!,
    from.pos,
    to,
    from.blockPos.down(),
    predict,
    0,
    terrain
)

fun damageByCrystal(
    from : BlockPos,
    to : PlayerEntity,
    predict : Boolean,
    terrain : Boolean
) = damageByCrystal(
    mc.world!!,
    Vec3d(from.x + 0.5, from.y + 1.0, from.z + 0.5),
    to,
    from,
    predict,
    0,
    terrain
)

fun sphere(
    center : BlockPos,
    radius : Int
) = mutableListOf<BlockPos>().also {
    for(x in -radius..radius) {
        for(y in -radius..radius) {
            for(z in -radius..radius) {
                val pos = BlockPos(x, y, z).add(center)

                it.add(pos)
            }
        }
    }
}

fun sphere(
    center : BlockPos,
    radius : Int,
    block : (BlockPos) -> Unit
) {
    for(x in -radius..radius) {
        for(y in -radius..radius) {
            for(z in -radius..radius) {
                val pos = BlockPos(x, y, z).add(center)

                block(pos)
            }
        }
    }
}

//TODO: refactor if statement
fun crystalable(
    pos : BlockPos,
    multiplace : Boolean? = null,
    entityCheck : Boolean? = null,
    strictDirectionCheck : Boolean? = null,
    removed : Set<Int> = emptySet()
) : Boolean {
    val obbyBlock = pos.block()
    val airBlock1 = pos.up().block()
    val airBlock2 = pos.up(2).block()
    val side = pos.reachableSide(full = false)

    return when(obbyBlock) {
        Blocks.OBSIDIAN, Blocks.BEDROCK -> {
            if(
                when(airBlock1) {
                    Blocks.AIR -> true
                    Blocks.WATER, Blocks.LAVA -> CrystalPlacementController.LIQUID_PLACE.value
                    Blocks.FIRE, Blocks.SOUL_FIRE -> CrystalPlacementController.FIRE_PLACE.value
                    else -> false
                }
                &&
                when(airBlock2) {
                    Blocks.AIR -> true
                    Blocks.WATER, Blocks.LAVA -> CrystalPlacementController.LIQUID_PLACE.value
                    else -> CrystalPlacementController.PROTOCOL.valEnum == Protocols.New
                }
                &&
                (!(strictDirectionCheck ?: CrystalPlacementController.STRICT_DIRECTION.value) || side != null)
            ) {
                !(entityCheck ?: CrystalPlacementController.ENTITY_CHECK.value) || !intersects(
                    Box(
                        pos.x.toDouble(),
                        pos.y.toDouble() + 1,
                        pos.z.toDouble(),
                        pos.x.toDouble() + 1,
                        pos.y.toDouble() + if(CrystalPlacementController.PROTOCOL.valEnum == Protocols.New) 2 else 3,
                        pos.z.toDouble() + 1
                    )
                ) {
                    (it !is EndCrystalEntity || multiplace ?: CrystalPlacementController.MULTIPLACE.value) && !removed.contains(it.id)
                }
            } else {
                false
            }
        }

        else -> false
    }
}

fun moving() = mc.player!!.input.movementForward != 0f || mc.player!!.input.movementSideways != 0f

fun rightClickBlock(
    pos : BlockPos,
    hand : Hand = Hand.MAIN_HAND,
    packet : Boolean = true,
    rotate : Rotates = Rotates.None,
    swing : Boolean = false,
    direction : Direction = Direction.UP,
    strictDirection : Boolean = false
) {
    val fixedDirection = if(strictDirection) {
        pos.reachableSide()
    } else {
        direction
    }

    if(fixedDirection != null) {
        val result = BlockHitResult(Vec3d.of(pos), fixedDirection, pos, false)

        //TODO: rotate to centre of side
        //TODO: use block placement controller
        rotate(pos, rotate) {
            if(packet) {
                (mc.interactionManager as InvokerClientPlayerInteractionManager).sendSequencedPacket0(mc.world!!) {
                    PlayerInteractBlockC2SPacket(hand, result, it)
                }
//                mc.player!!.networkHandler.sendPacket(PlayerInteractBlockC2SPacket(hand, result, 1))
            } else {
                mc.interactionManager!!.interactBlock(mc.player!!, hand, result)
            }

            if(swing) {
                mc.player!!.swingHand(hand)
            }
        }
    }
}

fun leftClickEntity(
    entity : Entity,
    hand : Hand = Hand.MAIN_HAND,
    packet : Boolean = true,
    rotate : Rotates = Rotates.None,
    swing : Boolean = false,
    reset : Boolean = false
) {
    rotate(entity, rotate) {
        if(packet) {
            mc.player!!.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player!!.isSneaking))

            if(reset) {
                mc.player!!.resetLastAttackedTicks()
            }
        } else {
            mc.interactionManager!!.attackEntity(mc.player!!, entity)
        }

        if(swing) {
            mc.player!!.swingHand(hand)
        }
    }
}

fun placeableSide(
    centre : BlockPos,
    airplace : Boolean = BlockPlacementController.AIRPLACE.value,
    strictDirection : Boolean = BlockPlacementController.STRICT_DIRECTION.value,
    placed : Collection<BlockPos> = emptyList()
) : Direction? {
    for(direction in Direction.values()) {
        val pos = centre.offset(direction)
        val block = pos.block()

        if(block != Blocks.AIR || placed.contains(pos) || airplace) {
            //TODO: refactor this if statement(i will do it myself xd)
            if(strictDirection) {
                val sides = pos.reachableSides()

                if(sides.contains(direction.opposite)) {
                    return direction
                }
            } else {
                return direction
            }
        }
    }

    return null
}

//TODO: longer of ways of helping blocks
fun placeBlock(
    pos : BlockPos,
    item : Item,
    hand : Hand = Hand.MAIN_HAND,
    packet : Boolean = true,
    swing : Boolean = false,
    airplace : Boolean = BlockPlacementController.AIRPLACE.value,
    strictDirection : Boolean = BlockPlacementController.STRICT_DIRECTION.value,
    helpingBlocks : Boolean = BlockPlacementController.HELPING_BLOCKS.value,
    placed : Collection<BlockPos> = emptyList()
) : Boolean {
    val slot = InventoryLocations.Hotbar.findInventoryItem(item)

    if(slot != -1) {
        val block = pos.block()

        if(block == Blocks.AIR) {
            var direction = placeableSide(pos, airplace, strictDirection, placed)

            if(direction == null && helpingBlocks) {
                val helpingBlock = pos.helpingBlock(placed)

                if(helpingBlock != null) {
                    if(placeBlock(helpingBlock, item, hand, packet, swing, airplace, strictDirection, false, placed)) {
                        direction = placeableSide(pos, airplace, strictDirection, listOf(helpingBlock))
                    }
                }
            }

            if(direction != null) {
                val offset = pos.offset(direction)
                val opposite = direction.opposite

                BlockPlacementController.place(offset, slot, pos) {
                    rightClickBlock(offset, hand, packet, Rotates.None, swing, opposite)
                }

                return true
            }
        }
    }

    return false
}

fun feetBlocks(
    entity : Entity,
    offset : Int = 0
) = hashSetOf<BlockPos>().also {
    val box = entity.boundingBox

    val minX = floor(box.minX).toInt()
    val minZ = floor(box.minZ).toInt()
    val maxX = floor(box.maxX - ALMOST_ZERO).toInt()
    val maxZ = floor(box.maxZ - ALMOST_ZERO).toInt()

    val y = entity.y.toInt() + offset

    it.add(BlockPos(minX, y, minZ))
    it.add(BlockPos(maxX, y, minZ))
    it.add(BlockPos(minX, y, maxZ))
    it.add(BlockPos(maxX, y, maxZ))
}

fun dynamicBlocks(
    entity : Entity,
    offset : Int = 0
) = mutableListOf<BlockPos>().also {it0 ->
    val base = feetBlocks(entity)

    for(pos in base) {
        pos.north().also { it1 -> if(!base.contains(it1)) it0.add(it1.up(offset)) }
        pos.south().also { it1 -> if(!base.contains(it1)) it0.add(it1.up(offset)) }
        pos.west().also { it1 -> if(!base.contains(it1)) it0.add(it1.up(offset)) }
        pos.east().also { it1 -> if(!base.contains(it1)) it0.add(it1.up(offset)) }
    }
}

fun dynamicBlocksSorted(
    entity : Entity
) : Map<Direction?, List<BlockPos?>> {
    val posses = dynamicBlocks(entity)
    val map = mutableMapOf<Direction?, List<BlockPos?>>()
    val entityPosition = entity.blockPos

    for(pos in posses) {
        if(pos.x == entityPosition.x || pos.z == entityPosition.z) {
            var pair : BlockPos? = null

            pos.north().also { if(posses.contains(it)) pair = it }
            pos.south().also { if(posses.contains(it)) pair = it }
            pos.west().also { if(posses.contains(it)) pair = it }
            pos.east().also { if(posses.contains(it)) pair = it }

            val diffX = (pos.x - entityPosition.x).coerceIn(-1..1)
            val diffZ = (pos.z - entityPosition.z).coerceIn(-1..1)
            val vec = Vec3i(diffX, 0, diffZ)
            var facing : Direction? = null

            for(facing0 in Direction.values()) {
                if(facing0.vector == vec) {
                    facing = facing0

                    break
                }
            }

            map[facing] = listOf(pos, pair)
        }
    }

    return map
}

fun placeable(
    pos : BlockPos,
    excludeEntity : Entity? = null,
    strictDirection : Boolean = BlockPlacementController.STRICT_DIRECTION.value
) = pos.reachable &&
        //TODO: rewrite this check
        pos.block() == Blocks.AIR &&
        !intersects(Box(pos)) {
            it !is ItemEntity && it !is ExperienceOrbEntity && it !is ArrowEntity && it != excludeEntity
        } &&
        (!strictDirection || pos.reachableSides().isNotEmpty())

fun intersects(
    box : Box,
    filter : (Entity) -> Boolean
) : Boolean {
    val lookup = (mc.world!! as InvokerWorld).entityLookup

    if(lookup is SimpleEntityLookup<Entity>) {
        val cache = (lookup as AccessorSimpleEntityLookup).cache<Entity>()
        val positions = (cache as AccessorSectionedEntityCache).trackedPositions
        val sections = (cache as AccessorSectionedEntityCache).trackingSections<Entity>()

        val minX = ChunkSectionPos.getSectionCoord(box.minX - 2)
        val minY = ChunkSectionPos.getSectionCoord(box.minY - 2)
        val minZ = ChunkSectionPos.getSectionCoord(box.minZ - 2)
        val maxX = ChunkSectionPos.getSectionCoord(box.maxX + 2)
        val maxY = ChunkSectionPos.getSectionCoord(box.maxY + 2)
        val maxZ = ChunkSectionPos.getSectionCoord(box.maxZ + 2)

        for(x in minX..maxX) {
            val x1 = ChunkSectionPos.asLong(x, 0, 0)
            val x2 = ChunkSectionPos.asLong(x, -1, -1)
            val iterator = positions.subSet(x1, x2 + 1).iterator()

            while(iterator.hasNext()) {
                val next = iterator.nextLong()
                val y = ChunkSectionPos.unpackY(next)
                val z = ChunkSectionPos.unpackZ(next)

                if(y in minY..maxY && z in minZ..maxZ) {
                    val section = sections.get(next)

                    if(section != null && section.status.shouldTrack()) {
                        for(entity in (section as AccessorEntityTrackingSection).collection<Entity>().copy()) {
                            if(entity.boundingBox.intersects(box) && filter(entity)) {
                                return true
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    val found = AtomicBoolean(false)

    lookup.forEachIntersects(box) {
        if(!found.get() && filter(it)) {
            found.set(true)
        }
    }

    return found.get()
}

fun highlight(
    posses : List<BlockPos>
) = mutableListOf<BlockPos>().also { it0 ->
    for(pos in posses) {
        pos.north().also { it1 -> if(!posses.contains(it1)) it0.add(it1) }
        pos.south().also { it1 -> if(!posses.contains(it1)) it0.add(it1) }
        pos.west().also { it1 -> if(!posses.contains(it1)) it0.add(it1) }
        pos.east().also { it1 -> if(!posses.contains(it1)) it0.add(it1) }
    }
}

fun make(
    vararg posses : BlockPos
) : Box {
    var box : Box? = null

    for(pos in posses) {
        box = if(box == null) {
            Box(pos)
        } else {
            box.union(Box(pos))
        }
    }

    return box!!
}

fun sendPacketNoEvent(
    packet : Packet<*>
) {
    (mc.networkHandler!!.connection as IClientConnection).sendNoEvent(packet)
}

fun visible(
    block : () -> Boolean
) = object : IVisible {
    override fun visible() = block()
}

fun breakingSpeed(
    state : BlockState,
    stack : ItemStack
) : Float {
    var multiplier = stack.getMiningSpeedMultiplier(state)

    if(multiplier > 1f && !stack.isEmpty) {
        val efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack)

        if(efficiency > 1) {
            multiplier += efficiency * efficiency + 1
        }
    }

    if(StatusEffectUtil.hasHaste(mc.player)) {
        val amplifier = StatusEffectUtil.getHasteAmplifier(mc.player)

        multiplier *= 1 + (amplifier + 1) * 0.2f
    }

    if(mc.player!!.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
        val effect = mc.player!!.getStatusEffect(StatusEffects.MINING_FATIGUE)!!
        val amplifier = effect.amplifier

        multiplier *= when(amplifier) {
            0 -> 0.3f
            1 -> 0.09f
            2 -> 0.0027f
            else -> 8.1E-4F
        }
    }

    return multiplier
}

fun canHarvest(
    state : BlockState,
    stack : ItemStack
) = !state.isToolRequired || stack.isSuitableFor(state)

fun miningTime(
    pos : BlockPos,
    stack : ItemStack
) : Long {
    val state = pos.state()
    val multiplier = breakingSpeed(state, stack)
    val hardness = state.getHardness(mc.world, pos)
    val harvest = if(canHarvest(state, stack)) 30f else 100f
    val delta = multiplier / hardness / harvest
    val ticks = floor(1f / delta) + 1f

    return ((ticks / 20f) * 1000L).toLong()
}

fun findBestHotbarTool(
    pos : BlockPos
) = InventoryLocations.Hotbar.findBestInventoryTool(pos)

fun breakingProgress(
    pos : BlockPos,
    stack : ItemStack,
    start : Long
) = (1.0 - (System.currentTimeMillis() - start).toDouble() / miningTime(pos, stack).toDouble()).coerceIn(0.0..1.0)

fun Block.safe() = safeBlocks.contains(this)

fun ping() = mc.player.ping()

fun Entity?.ping() = if(mc.isInSingleplayer || this == null) 0 else mc.networkHandler!!.getPlayerListEntry(uuid)?.latency ?: -2

//TODO: convert to field
fun BlockPos.vec() = Vec3d(
    x.toDouble() + 0.5,
    y.toDouble() + 0.5,
    z.toDouble() + 0.5
)

fun currentServerIP() = mc.currentServerEntry?.address ?: "Singleplayer"

fun debug(
    any : Any
) {
    if(LavaHack.DEBUG) {
        println("Debug >> $any")
    }
}

fun <T : Comparable<T>> T.coerceIn(
    range : ClosedFloatingPointRange<T>
) = when {
    range.lessThanOrEquals(this, range.start) && !range.lessThanOrEquals(range.start, this) -> range.start
    range.lessThanOrEquals(range.endInclusive, this) && !range.lessThanOrEquals(this, range.endInclusive) -> range.endInclusive
    else -> this
}

var ClientPlayerEntity.velocityX
    get() = velocity.x
    set(value) {
        setVelocity(value, velocity.y, velocity.z)
    }

var ClientPlayerEntity.velocityY
    get() = velocity.y
    set(value) {
        setVelocity(velocity.x, value, velocity.z)
    }

var ClientPlayerEntity.velocityZ
    get() = velocity.z
    set(value) {
        setVelocity(velocity.x, velocity.y, value)
    }

fun hotbar2inventory(
    slot : Int
) = when (slot) {
    -2 -> 45
    in 0..8 -> slot + 36
    else -> slot
}

fun String.friend() = Friends.names.contains(lowercase())

fun String.enemy() = lowercase() == nearest?.name?.string?.lowercase()

fun PlayerEntity.friend() = name.string.friend()

fun PlayerEntity.enemy() = name.string.enemy()

/**
 * @author cattyngmd
 */
val PlayerInteractEntityC2SPacket.type : InteractTypes
    get() = PacketByteBuf(Unpooled.buffer()).also {
        write(it)

        it.readVarInt()
    }.readEnumConstant(InteractTypes::class.java)

operator fun Number.plus(
    number : Number
) = (this.toDouble() + number.toDouble()) as Number

operator fun Number.minus(
    number : Number
) = (this.toDouble() - number.toDouble()) as Number

operator fun Number.times(
    number : Number
) = (this.toDouble() * number.toDouble()) as Number

operator fun Number.div(
    number : Number
) = (this.toDouble() / number.toDouble()) as Number

operator fun Number.compareTo(
    number : Number
) = this.toDouble().compareTo(number.toDouble())

operator fun Number.unaryMinus() = -toDouble() as Number

val Direction.left
    get() = when(this) {
        Direction.NORTH -> Direction.WEST
        Direction.WEST -> Direction.SOUTH
        Direction.SOUTH -> Direction.EAST
        Direction.EAST -> Direction.NORTH
        else -> this
    }

val Direction.right
    get() = if(this.horizontal == -1) {
        this
    } else {
        this.left.opposite
    }

fun BlockPos.helpingBlock(
    placed : Collection<BlockPos> = emptyList()
) : BlockPos? {
    for(direction in Direction.values()) {
        val offset = offset(direction)

        if(placeable(offset) && placeableSide(offset, placed = placed) != null) {
            return this
        }
    }

    return null
}

val cevOffsets = listOf(
    BlockPos(0, 2, 0),
    BlockPos(1, 1, 0),
    BlockPos(-1, 1, 0),
    BlockPos(0, 1, 1),
    BlockPos(0, 1, -1)
)

fun BlockPos.cev(
    centre : BlockPos
) : Boolean {
    for(cevOffset in cevOffsets) {
        val offset = centre.add(cevOffset)

        if(this == offset) {
            return true
        }
    }

    return false
}

/*
fun smartRangeCheck(
    entity : Entity,
    pos : BlockPos
) : Boolean {
    val box = pos.box()
    val vec = pos.vec()
    val diff = vec.subtract(entity.pos)

//    val x = if(diff.x)

//    val nearest = Vec3d()
}*/

fun handleSetDead(
    entity : Entity,
    setDead : Boolean,
    removeEntity : Boolean,
) {
    if(setDead) {
        entity.setRemoved(Entity.RemovalReason.KILLED)
    }

    if(removeEntity) {
        val index = ((mc.world!! as InvokerWorld).entityLookup as AccessorSimpleEntityLookup).index<Entity>()

        index.remove(entity)
    }
}

fun countItems(
    filter : Item,
    location : InventoryLocations
) : Int {
    var counter = 0

    for(slot in location.inventorySlots) {
        val stack = mc.player!!.inventory.getStack(slot)!!
        val item = stack.item!!
        val count = stack.count

        if(filter == item) {
            counter += count
        }
    }

    return counter
}

/**
 * @author cattyngmd
 */
fun BlockPos.reachableSides(
    placeableCheck : Boolean = true,
    full : Boolean = true
) = if(full) {
    Direction.values().toMutableList().also {
        if(placeableCheck) {
            for(direction in Direction.values()) {
                val offset = offset(direction)
                val state = offset.state()

                if(!state.isReplaceable) {
                    it.remove(direction)
                }
            }
        }

        val eyes = mc.player!!.eyePos
        val center = toCenterPos()

        val diffX = eyes.x - center.x
        val diffZ = eyes.z - center.z

        if(diffX > 0.5) {
            it.remove(Direction.WEST)
        } else if(diffX < -0.5) {
            it.remove(Direction.EAST)
        } else {
            it.remove(Direction.WEST)
            it.remove(Direction.EAST)
        }

        if(diffZ > 0.5) {
            it.remove(Direction.NORTH)
        } else if(diffZ < -0.5) {
            it.remove(Direction.SOUTH)
        } else {
            it.remove(Direction.NORTH)
            it.remove(Direction.SOUTH)
        }

        if(eyes.y < y + 1) {
            it.remove(Direction.UP)
        }

        if(eyes.y > y) {
            it.remove(Direction.DOWN)
        }
    }
} else {
    val eyes = mc.player!!.eyePos

    if(eyes.y >= y + 1) {
        mutableListOf(Direction.UP)
    } else {
        mutableListOf(Direction.DOWN)
    }
}

fun BlockPos.reachableSide(
    placeableCheck : Boolean = true,
    full : Boolean = true
) = reachableSides(placeableCheck, full).firstOrNull()

/**
 * @author cattyngmd
 */
fun Int.asString() = if(this in 101..999) {
    "%.1fk".format(this / 1000f)
} else {
    toString()
}

infix fun (() -> Boolean).and(
    other : () -> Boolean
) : () -> Boolean = { this() && other() }

infix fun (() -> Boolean).or(
    other : () -> Boolean
) : () -> Boolean = { this() || other() }

operator fun (() -> Boolean).not() : () -> Boolean = { !this() }


val ByteArray.buffer get() = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).put(this).flip()!!

val InputStream.buffer get() = this.readBytes().buffer!!

val File.buffer get() = if(exists() && isFile) {
    FileInputStream(this).buffer
} else {
    null
}

val Vec3d.yaw get() = MathHelper.wrapDegrees(atan2(z - mc.player!!.eyePos.z, x - mc.player!!.eyePos.x) * DEG - 90f)

val Vec3d.pitch get() = MathHelper.wrapDegrees(-atan2(y - mc.player!!.eyePos.y, hypotenuse(x + 0.5 - mc.player!!.eyePos.x, z + 0.5 - mc.player!!.eyePos.z)) * DEG)

val Vec3d.rotates get() = yaw to pitch

val BlockPos.rotates get() = toCenterPos().rotates

val Entity.rotates get() = when(RotationSystem.ENTITY_ROTATE.valEnum) {
    EntityRotates.HEAD -> pos.add(0.0, getEyeHeight(pose).toDouble(), 0.0)!!
    EntityRotates.BODY -> pos.add(0.0, height / 2.0, 0.0)!!
    EntityRotates.FEET -> pos
}.run { yaw to pitch }

val Box.rotates get() = center.rotates

fun Box.rotates(
    direction : Direction
) : Pair<Double, Double> {
    val offset = Vec3d(direction.unitVector)
    val length = Vec3d(xLength, yLength, zLength).multiply(0.5)
    val center = center.add(length.multiply(offset))

    return center.rotates
}

fun BlockPos.rotates(
    direction : Direction
) = box().rotates(direction)

val (Pair<Double, Double>).vector : Vec3d get() {
    val yawCos = cos(-first.wrap2() * RAD - PI).toDouble()
    val yawSin = sin(-first.wrap2() * RAD - PI).toDouble()
    val pitchCos = -cos(-second * RAD).toDouble()
    val pitchSin = sin(-second * RAD).toDouble()

    return Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos)
}

fun packetRotate(
    angles : Pair<Number, Number>,
    block : () -> Unit
) {
    val yaw = angles.first.toFloat()
    val pitch = angles.second.toFloat()
    val ground = mc.player!!.isOnGround

    mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, ground))

    block()
}

fun Entity.type() : Entities? {
    for(entity in Entities.values()) {
        if(entity.filter(this)) {
            return entity
        }
    }

    return null
}

val BlockPos.reachable get() = mc.player!!.pos distanceSq this.vec() < BlockPlacementController.REACH_DISTANCE.value.square()

//Converts yaw from [-180; 180] to [0; 360)
fun Float.wrap() = if(this < 0) {
    -this
} else {
    -this + 360f
}

fun Double.wrap2() = if(this > 180) {
    this - 360.0
} else if(this < 180) {
    this + 360.0
} else {
    this
}

fun VertexConsumer.vertex(
    matrix : Matrix4f,
    vec : Vec3d
) = vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat())!!

fun VertexConsumer.normal(
    matrix : Matrix3f,
    vec : Vec3d
) = normal(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat())!!

fun setScreenSilently(
    screen : Screen?
) {
    ScreenEvent.Open.STATE = false

    mc.setScreen(null)

    ScreenEvent.Open.STATE = true
}

fun context(
    start : Vec3d,
    end : Vec3d
) = RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)

fun raycast(
    start : Vec3d,
    end : Vec3d
) = mc.world!!.raycast(context(start, end))

fun traceable(
    start : Vec3d,
    end : Vec3d
) = raycast(start, end)?.pos == end

//val Entity.traceable get() = traceable(mc.player!!.eyePos, )

fun cooldown(
    slot : Int
) : Float {
    val prev = mc.player!!.inventory.selectedSlot

    mc.player!!.inventory.selectedSlot = slot

    return mc.player!!.getAttackCooldownProgress(0f).also {
        mc.player!!.inventory.selectedSlot = prev
    }
}

//TODO: should be updated after moving on newest version
@ManualUpdate
val SUPER_SECRET_SETTING_PROGRAMS = arrayOf(
    Identifier("shaders/post/notch.json"),
    Identifier("shaders/post/fxaa.json"),
    Identifier("shaders/post/art.json"),
    Identifier("shaders/post/bumpy.json"),
    Identifier("shaders/post/blobs2.json"),
    Identifier("shaders/post/pencil.json"),
    Identifier("shaders/post/color_convolve.json"),
    Identifier("shaders/post/deconverge.json"),
    Identifier("shaders/post/flip.json"),
    Identifier("shaders/post/invert.json"),
    Identifier("shaders/post/ntsc.json"),
    Identifier("shaders/post/outline.json"),
    Identifier("shaders/post/phosphor.json"),
    Identifier("shaders/post/scan_pincushion.json"),
    Identifier("shaders/post/sobel.json"),
    Identifier("shaders/post/bits.json"),
    Identifier("shaders/post/desaturate.json"),
    Identifier("shaders/post/green.json"),
    Identifier("shaders/post/blur.json"),
    Identifier("shaders/post/wobble.json"),
    Identifier("shaders/post/blobs.json"),
    Identifier("shaders/post/antialias.json"),
    Identifier("shaders/post/creeper.json"),
    Identifier("shaders/post/spider.json")
)

class SuperSecretProgram(
    private val name : String,
    val index : Int
) {
    override fun toString() = name
}

val superSecretPrograms : Element<SuperSecretProgram>
    get() {
        val programs = mutableListOf<SuperSecretProgram>()

        for((index, identifier) in SUPER_SECRET_SETTING_PROGRAMS.withIndex()) {
            val path = identifier.path
            val split = path.split("/")
            val name = split[split.lastIndex].removeSuffix(".json")
            val program = SuperSecretProgram(name, index)

            programs.add(program)
        }

        return Element(programs.first(), programs)
    }

const val DEFAULT_BACKGROUND_COLOR = -1072689136

fun MatrixStack.scale(
    translateX : Number,
    translateY : Number,
    translateZ : Number,
    scaleX : Number,
    scaleY : Number,
    scaleZ : Number,
    block : () -> Unit
) {
    push()
    translate(translateX.toDouble(), translateY.toDouble(), translateZ.toDouble())
    scale(scaleX.toFloat(), scaleY.toFloat(), scaleZ.toFloat())
    block()
    pop()
}

fun MatrixStack.scale(
    translateX : Number,
    translateY : Number,
    scaleX : Number,
    scaleY : Number,
    block : () -> Unit
) {
    scale(
        translateX,
        translateY,
        0,
        scaleX,
        scaleY,
        1,
        block
    )
}

fun MatrixStack.scale(
    translateX : Number,
    translateY : Number,
    scale : Number,
    block : () -> Unit
) {
    if(scale != 1.0) {
        scale(
            translateX,
            translateY,
            scale,
            scale,
            block
        )
    }
}

val ItemStack.durability get() = (maxDamage - damage).toDouble() / maxDamage.toDouble()

val ItemStack.durabilityColor get() = Color.GREEN.mix(Color.RED, durability).brightness(1)

fun Color.mix(
    color : Color,
    progress : Number
) = Color(
    (red * progress.toDouble() + color.red * (1.0 - progress.toDouble())).toInt().coerceIn(0..255),
    (green * progress.toDouble() + color.green * (1.0 - progress.toDouble())).toInt().coerceIn(0..255),
    (blue * progress.toDouble() + color.blue * (1.0 - progress.toDouble())).toInt().coerceIn(0..255),
    (alpha * progress.toDouble() + color.alpha * (1.0 - progress.toDouble())).toInt().coerceIn(0..255)
)

//TODO: rewrite with using block states
val UNBREAKABLE_BLOCKS = arrayOf(
    Blocks.BARRIER,
    Blocks.BEDROCK,
    Blocks.COMMAND_BLOCK,
    Blocks.STRUCTURE_BLOCK,
    Blocks.STRUCTURE_VOID,
    Blocks.LIGHT,
    Blocks.END_PORTAL,
    Blocks.NETHER_PORTAL,
    Blocks.END_PORTAL_FRAME
)

val Block.breakable get() = !UNBREAKABLE_BLOCKS.contains(this)
val Block.unbreakable get() = !breakable

val BlockPos.breakable get() = block().breakable
val BlockPos.unbreakable get() = block().unbreakable

fun Vector4i.copy() = Vector4i(x(), y(), z(), w())

fun Vector4i.color() = Colour(x(), y(), z(), w())

val Color.veci get() = Vector4i(red, green, blue, alpha)

fun Color.alpha(
    alpha : Number
) = Color(red, green, blue, if(alpha is Int) alpha else (alpha.toDouble() * 255.0).toInt().coerceIn(0..255))

//TODO: what about Utility::Color.mix????
fun mix(
    progress : Float,
    color1 : Color,
    color2 : Color,
    brightness : Float = 1.0f
) = lerp(color1, color2, progress)

//TODO: i want just begin/end/draw methods

fun SettingEnum<CoreShaders>.beginScreen() = valEnum.screenShader.begin()
fun SettingEnum<CoreShaders>.endScreen() = valEnum.screenShader.end()

fun SettingEnum<CoreShaders>.beginWorld() = valEnum.worldShader.begin()
fun SettingEnum<CoreShaders>.endWorld() = valEnum.worldShader.end()

fun SettingEnum<CoreShaders>.drawWorld(
    block : () -> Unit
) {
    beginWorld()
    block()
    endWorld()
}

fun SettingEnum<CoreShaders>.drawScreen(
    block : () -> Unit
) {
    beginScreen()
    block()
    endWorld()
}

fun <T : Enum<T>> T.element() = Element(this, this.javaClass.enumConstants.toList())

fun animate(
    current : Number,
    target : Number,
    speed : Number
) = (current + (target - current) * speed).toDouble()