package lavahack.client.settings

import lavahack.client.features.config.StoredData
import lavahack.client.utils.asString
import lavahack.client.utils.client.interfaces.*
import lavahack.client.utils.client.interfaces.impl.SettingRegistry

/**
 * @author _kisman_
 * @since 15:18 of 09.05.2023
 */
@Suppress("UNCHECKED_CAST")
open class Setting<T : Any>(
    var name : String,
    val default : T,
    val title : String = name,
    var onChange : (Setting<T>) -> Unit = {  }
) : ISettingRegistry, IVisible, IStorable, () -> T {
    override val registry = SettingRegistry()

    private var linkingState : (() -> Boolean)? = null
    private var linkedValue : (() -> T)? = null

    open var value : T = default
        get() {
            return if(linkingState?.invoke() == true) {
                linkedValue!!.invoke()
            } else {
                field
            }
        }
        set(value) {
            prev = field
            field = value
            onChange(this)
        }

    var prev : T? = null

    var bound : ISettingRegistry? = null
    var prefix : IPrefixable? = null

    var savable = true

    private var _visible = { true }

    open fun link(
        state : () -> Boolean,
        value : () -> T,
        visible : (() -> Boolean)? = { !state() }
    ) = this.also {
        linkingState = state
        linkedValue = value

        if(visible != null) {
            visible(visible)
        }
    }

    open fun link(
        pair : Pair<() -> Boolean, () -> T>
    ) = link(pair.first, pair.second)

    open fun visible(
        value : () -> Boolean
    ) = this.also {
        _visible = value
    }

    open fun onRegister(
        registry : ISettingRegistry
    ) {
        if(bound == null) {
            bound = registry
        }

        if(registry is IPrefixable) {
            prefix = registry
        }
    }

    override fun invoke() = value

    override fun visible() = _visible()

    override fun save() = if(default is IStorable) {
        val data = default.save()

        if(prefix != null) {
            data.prefix = "${prefix!!.prefix()}.setting.$name"
        }

        data
    } else {
        StoredData(
            if(prefix != null) "${prefix!!.prefix()}.setting.$name"
            else "setting.$name",
            "value", asString(value)
        )
    }

    override fun load(
        data : StoredData
    ) {
        if(default is IStorable) {
            default.load(data)
            onChange(this)
        } else {
            value = data.type("value", default) ?: value
        }
    }

    class Caster<T : Any> : ICaster<Setting<T>> {
        override fun cast(
            setting : Any
        ) = setting as Setting<T>
    }

    class Fast<T: Any>(
        setting : Setting<T>
    ) {
        var value : T = setting.value

        init {
            setting.onChange = { value = it.value }
        }
    }
}