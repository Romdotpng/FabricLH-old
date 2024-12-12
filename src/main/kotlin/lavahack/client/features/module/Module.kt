package lavahack.client.features.module

import lavahack.client.LavaHack
import lavahack.client.callback.Callback
import lavahack.client.features.DisplayableFeature
import lavahack.client.features.config.StoredData
import lavahack.client.features.subsystem.subsystems.*
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.chat.ChatUtility
import lavahack.client.utils.client.enums.BindTypes
import lavahack.client.utils.client.interfaces.*
import lavahack.client.utils.client.interfaces.impl.*
import lavahack.client.utils.threads.handle
import net.minecraft.client.MinecraftClient
import kotlin.reflect.KClass

/**
 * @author _kisman_
 * @since 3:43 of 08.05.2023
 */
@Suppress("LeakingThis")
open class Module : DisplayableFeature(
    buttonName = "Bind"
), IPrefixable, ICallbackRegistry, IListenerRegistry {
    val info = javaClass.getAnnotation(Info::class.java)!!
    val name = info.display.ifEmpty { info.name }
    var visible = info.visible
    var displayInfo : () -> String? = { null }
    var guiVisible = true

    var state = false
        set(value) {
            if(value != field && !toggling) {
                toggle(value)
            }

            field = value
        }

    val stateSupplier = { state }

    val submodules = mutableListOf<Module>()

    private var parent : Module? = null

    protected val mc = MinecraftClient.getInstance()!!

    private val threadsGroup = SettingGroup("Threads").visible { callbacks.callbacks.contains(2) }
    private val threadsState = threadsGroup.add(Setting("State", false))
    private val threadsDelay = threadsGroup.add(SettingNumber("Delay", 15L, 0L..100L))

    override val registry = SettingRegistry(this, mutableListOf<Setting<*>>(
        Setting("Bind", this as IBindable).visible { info.properties.bind }.also { it.savable = false },
        Setting("Hold Bind", false) { hold = it.value }.visible { info.properties.bind }.also { it.savable = false },
        Setting("Visible", info.visible) { visible = it.value }.visible { info.properties.visible }.also { it.savable = false },
        threadsGroup,
        threadsState,
        threadsDelay
    )).also {
        it.addAll(super.registry.settings)
    }

    override val callbacks = CallbackRegistry()
    override val listeners = ListenerRegistry()

    private var toggling = false

    private val enemyFinder : IEnemyFinder?

    protected val enemy get() = enemyFinder?.enemy

    protected var selfAsEnemy = { false }

    init {
        threadsGroup.prefix("Threads")

        super.keyboardKey = info.key

        if(info.holeprocessor) {
            HoleProcessor.states.add(stateSupplier)
        }

        enemyFinder = if(info.targetable.exists) {
            displayInfo = { enemy?.name?.string ?: if(selfAsEnemy() && info.targetable.self) "self" else "" }

            if(info.targetable.nearest) {
                EnemyManager.NEAREST_ENEMY_FINDER
            } else {
                val clazz = info.targetable.finder

                if(clazz != DummyEnemyFinder::class) {
                    try {
                        clazz.java.getConstructor().newInstance().also {
                            EnemyManager.finders.add(stateSupplier to it)
                        }
                    } catch(
                        throwable : Throwable
                    ) {
                        LavaHack.LOGGER.error("Failed creating instance of a enemy finder of ${info.name} module!", throwable)

                        null
                    }
                } else {
                    null
                }
            }
        } else {
            null
        }

        for(clazz in info.modules) {
            try {
                val submodule = clazz.java.getConstructor().newInstance()

                submodule.parent = this
                submodules.add(submodule)
            } catch(
                throwable : Throwable
            ) {
                LavaHack.LOGGER.error("Failed creating instance of a submodule of ${info.name} module!", throwable)
            }
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                return@tickListener
            }

            val callbacks = callbacks.callbacks

            if(callbacks.contains(2)) {
                val threadCallbacks = callbacks[2]!!
                //TODO: remove firstOrNull
                val threadCallback = threadCallbacks.firstOrNull { it is Callback.Delayed } as Callback.Delayed?

                threadCallback?.handle(threadsState.value, threadsDelay.value)
            }
        }
    }

    fun post() {
        if(info.state) {
            toggle(true)
        }
    }

    fun toggle() {
        toggle(!state)
    }

    fun toggle(
        state : Boolean
    ) {
        toggling = true

        this.state = state

        toggling = false

        if(state) {
            enable()
        } else {
            disable()
        }
    }

    private fun enable() {
        try {
            callbacks[0]
            onEnable()
        } catch(error : Error) {
            val text = "Received error while enabling ${info.name} module!"

            LavaHack.LOGGER.error(text, error)

            if(mc.player != null && mc.world != null) {
                ChatUtility.INFO.print(text)
            }
        }

        listeners.subscribe()

        if(info.messages) {
            ChatUtility.ENABLE.print(info.name)
        }
    }

    private fun disable() {
        try {
            callbacks[1]
            onDisable()
        } catch(error : Error) {
            val text = "Received error while disabling ${info.name} module!"

            LavaHack.LOGGER.error(text, error)

            if(mc.player != null && mc.world != null) {
                ChatUtility.INFO.print(text)
            }
        }

        listeners.unsubscribe()

        if(info.messages) {
            ChatUtility.DISABLE.print(info.name)
        }
    }

    open fun onEnable() { }
    open fun onDisable() { }

    override fun onInputEvent() {
        toggle()
    }

    override fun save() = StoredData(
        "module.${info.name}",
        "state", state,
        "visible", visible,
        "hold", hold,
        "key", keyboardKey,
        "button", mouseButton,
        "type", type
    )

    override fun load(
        data : StoredData
    ) {
        state = data.boolean("state") ?: state
        visible = data.boolean("visible") ?: visible
        hold = data.boolean("hold") ?: hold
        keyboardKey = data.int("key") ?: keyboardKey
        mouseButton = data.int("button") ?: mouseButton
        type = BindTypes.valueOf(data.string("type") ?: type.toString())
    }

    override fun prefix() = "module.${info.name}"

    override fun visible() = true

    override fun toString() = info.name

    enum class Category(
        val display : String
    ) {
        COMBAT("Combat"),
        CLIENT("Client"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        RENDER("Render"),
        MISC("Misc"),
        EXPLOIT("Exploit"),

        DEBUG("Debug"),
        WIP("WIP"),

        ADDONS("Addons")

        ;

        val modules = mutableListOf<Module>()
    }

    annotation class Info(
        val name : String,
        val display : String = "",
        val description : String = "",
        val aliases : String = "",
        val category : Category = Category.CLIENT,
        val state : Boolean = false,
        val visible : Boolean = true,
        val key : Int = -1,
        val beta : Boolean = false,
        val toggleable : Boolean = true,
        val targetable : Targetable = Targetable(exists = false),
        val properties : Properties = Properties(),
        val messages : Boolean = true,
        val submodule : Boolean = false,
        val holeprocessor : Boolean = false,
        vararg val modules : KClass<out Module>
    )

    annotation class Properties(
        val bind : Boolean = true,
        val visible : Boolean = true
    )
}