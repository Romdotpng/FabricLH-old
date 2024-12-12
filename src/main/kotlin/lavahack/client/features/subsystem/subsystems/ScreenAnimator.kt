package lavahack.client.features.subsystem.subsystems

import lavahack.client.event.events.ScreenEvent
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.settings.types.SettingEnum
import lavahack.client.settings.types.SettingGroup
import lavahack.client.settings.types.SettingNumber
import lavahack.client.utils.client.enums.Easings
import lavahack.client.utils.client.interfaces.impl.listener
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import lavahack.client.utils.mc
import lavahack.client.utils.setScreenSilently
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

@Suppress("unused")
object ScreenAnimator : SubSystem(
    "Screen Animator"
) {
    //TODO: change to init() method
    init {
        val openGroup = register(SettingGroup("Open"))
        val openAnimation = register(openGroup.add(SettingEnum("Animation", Animations.Off)))
        val openEasing = register(openGroup.add(SettingEnum("Easing", Easings.Linear)))
        val openLength = register(openGroup.add(SettingNumber("Length", 100L, 1L..1000L)))

        val closeGroup = register(SettingGroup("Close"))
        val closeAnimation = register(closeGroup.add(SettingEnum("Animation", Animations.Off)))
        val closeEasing = register(closeGroup.add(SettingEnum("Easing", Easings.Linear)))
        val closeLength = register(closeGroup.add(SettingNumber("Length", 100L, 1L..1000L)))

        openGroup.prefix("Open")
        closeGroup.prefix("Close")

        var currentScreen : Screen? = null
        var nextScreen : Screen? = null
        var openTimestamp = -1L
        var closeTimestamp = -1L

        fun reset() {
            currentScreen = null
            nextScreen = null
            openTimestamp = -1L
            closeTimestamp = -1L
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                reset()
            }
        }

        listener<ScreenEvent.Open> {
            val screen = it.screen

            if(mc.player == null || mc.world == null || screen is DownloadingTerrainScreen) {
                reset()

                return@listener
            }

            if(screen != currentScreen && currentScreen?.javaClass != screen?.javaClass) {
                if(screen == null || currentScreen != null) {
                    if(closeAnimation.valEnum != Animations.Off) {
                        openTimestamp = -1L
                        closeTimestamp = System.currentTimeMillis()
                        currentScreen = screen
                        nextScreen = screen

                        it.cancel()
                    }
                } else {
                    if(openAnimation.valEnum != Animations.Off) {
                        openTimestamp = System.currentTimeMillis()
                        closeTimestamp = -1L
                        currentScreen = screen
                    }
                }
            }

        }

        listener<ScreenEvent.Render.Pre> {
            it.context.matrices.push()

            if(openAnimation.valEnum != Animations.Off && openTimestamp != -1L) {
                val diff = System.currentTimeMillis() - openTimestamp
                val percent = diff.toDouble() / openLength.value.toDouble()

                if(diff > openLength.value) {
                    openTimestamp = -1L
                } else {
                    openAnimation.valEnum.animator(
                        it.context.matrices,
                        openEasing.valEnum,
                        percent
                    )
                }
            }

            if(closeAnimation.valEnum != Animations.Off && closeTimestamp != -1L) {
                val diff = System.currentTimeMillis() - closeTimestamp
                val percent = 1.0 - diff.toDouble() / closeLength.value.toDouble()

                if(diff > closeLength.value) {
                    closeTimestamp = -1L

                    if(nextScreen == null) {
                        currentScreen = null
                        setScreenSilently(null)
                    } else {
                        mc.setScreen(nextScreen)
                    }

                    it.cancel()
                } else {
                    closeAnimation.valEnum.animator(
                        it.context.matrices,
                        closeEasing.valEnum,
                        percent
                    )
                }
            }
        }

        listener<ScreenEvent.Render.Post> {
            it.context.matrices.pop()
        }
    }

    enum class Animations(
        val animator : (MatrixStack, Easings, Double) -> Unit
    ) {
        Off({ _, _, _ -> }),
        Drop({ matrices, easing, percent ->
            val factor = easing.dec(percent)
            val y = -mc.window!!.scaledHeight.toDouble() * factor

            matrices.translate(0.0, y, 0.0)
        }),
        Size({ matrices, easing, percent ->
            val factor = easing.inc(percent).toFloat()

            val translateX = (mc.window!!.scaledWidth.toDouble() / 2.0) * (1.0 - factor)
            val translateY = (mc.window!!.scaledHeight.toDouble() / 2.0) * (1.0 - factor)

            matrices.translate(translateX, translateY, 0.0)
            matrices.scale(factor, factor, 1f)
        })
    }
}