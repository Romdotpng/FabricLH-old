package lavahack.client.features.module.modules.player

import com.mojang.authlib.GameProfile
import lavahack.client.features.module.Module
import lavahack.client.features.module.disableCallback
import lavahack.client.features.module.enableCallback
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.client.network.OtherClientPlayerEntity

/**
 * @author _kisman_
 * @since 11:17 of 24.06.2023
 */
@Suppress("PrivatePropertyName")
@Module.Info(
    name = "FakePlayer",
    category = Module.Category.PLAYER
)
class FakePlayer : Module() {
    private var FAKEPLAYER : OtherClientPlayerEntity? = null

    init {
        enableCallback {
            if(mc.player == null || mc.world == null) {
                return@enableCallback
            }

            FAKEPLAYER = OtherClientPlayerEntity(mc.world!!, GameProfile(mc.player!!.uuid, "Z"))
            FAKEPLAYER!!.copyFrom(mc.player!!)

            mc.world!!.addEntity(-1, FAKEPLAYER)
        }

        disableCallback {
            if(mc.player == null || mc.world == null || FAKEPLAYER == null) {
                return@disableCallback
            }

            FAKEPLAYER!!.discard()
        }

        tickListener {
            if(mc.player == null || mc.world == null) {
                toggle(false)
            }
        }
    }
}