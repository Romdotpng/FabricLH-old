package lavahack.client.utils

import lavahack.client.features.config.StoredData
import lavahack.client.features.subsystem.subsystems.ColorManager
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.interfaces.ISettingRegistry
import lavahack.client.utils.client.interfaces.IStorable
import lavahack.client.utils.client.interfaces.impl.SettingRegistry
import lavahack.client.utils.client.interfaces.impl.register
import net.minecraft.text.TextColor
import org.joml.Vector4i
import java.awt.Color

/**
 * @author _kisman_
 * @since 11:29 of 02.06.2023
 */
class Colour(
    _value : Int,
    _sync : Boolean = false
) : Color(
    _value
), ISettingRegistry, IStorable {
    override val registry = SettingRegistry()

    private val sync = register(Setting("Sync", false).visible { ColorManager.SYNC_COLOR_VISIBILITY.visible() && !_sync })

    private val rgroup = register(SettingGroup("Rainbow"))
    private val rainbow = register(rgroup.add(Setting("Rainbow", false)))
    private val rsaturation = register(rgroup.add(SettingNumber("Saturation", 1f, 0f..1f)))
    private val rbrightness = register(rgroup.add(SettingNumber("Brightness", 1f, 0f..1f)))
    private val rlength = register(rgroup.add(SettingNumber("Length", 5000L, 100L..10000L)))
    private val roffset = register(rgroup.add(SettingNumber("Offset", 0f, 0f..1f)))

    val formatting get() = TextColor.fromRgb(rgb)

    private var value = _value
        get() = if(sync.value) {
            ColorManager.SYNC_COLOR.value.rgb
        } else {
            field
        }

    constructor(
        r : Int,
        g : Int,
        b : Int,
        a : Int = 255,
        sync : Boolean = false
    ) : this(
        Color(r, g, b, a.coerceIn(0, 255)).rgb,
        sync
    )

    constructor(
        r : Float,
        g : Float,
        b : Float,
        a : Float = 1f,
        sync : Boolean = false
    ) : this(
        Color(r, g, b, a).rgb,
        sync
    )

    fun hsb(
        _hue : Number? = null,
        _saturation : Number? = null,
        _brightness : Number? = null
    ) = this.also {
        val color = Color(value)
        val hsb = RGBtoHSB(color.red, color.green, color.blue, null)
        val hue = _hue?.toFloat() ?: hsb[0]
        val saturation = _saturation?.toFloat() ?: hsb[1]
        val brightness = _brightness?.toFloat() ?: hsb[2]

        it.value = HSBtoRGB(hue, saturation, brightness)

        alpha(color.alpha)
    }

    fun alpha(
        alpha : Int
    ) = this.also {
        val color = Color(value)
        val rgb = Color(color.red, color.green, color.blue, alpha.coerceIn(0..255)).rgb

        it.value = rgb
    }

    fun copy(
        color : Color
    ) {
        value = color.rgb
    }

    fun clone(
        full : Boolean = false
    ) = Colour(value).also {
        if(full) {
            val data = save()

            it.load(data)
        }
    }

    fun mix(
        color : Color,
        progress : Number,
        full : Boolean = false
    ) = Colour((this as Color).mix(color, progress).rgb).also {
        if(full) {
            val data = save()

            it.load(data)
        }
    }

    override fun getRGB() : Int {
        if(rainbow.value) {
            val time = System.currentTimeMillis()
            var hue = (time % rlength.value).toFloat() / rlength.value.toFloat()
            val saturation = rsaturation.value
            val brightness = rbrightness.value

            if(hue > 1f) {
                hue -= 1f
            }

            hsb(hue, saturation, brightness)
        }

        return value
    }

    override fun save() = StoredData(
        "NULL",
        "value", rgb,
        "sync", sync.value,
        "rainbow", rainbow.value,
        "rsaturation", rsaturation.value,
        "rbrightness", rbrightness.value,
        "rlength", rlength.value,
        "roffset", roffset.value
    )

    override fun load(
        data : StoredData
    ) {
        value = data.int("value") ?: value
        sync.value = data.boolean("sync") ?: sync.value
        rainbow.value = data.boolean("rainbow") ?: rainbow.value
        rsaturation.value = data.float("rsaturation") ?: rsaturation.value
        rbrightness.value = data.float("rbrightness") ?: rbrightness.value
        rlength.value = data.long("rlength") ?: rlength.value
        roffset.value = data.float("roffset") ?: roffset.value
    }
}