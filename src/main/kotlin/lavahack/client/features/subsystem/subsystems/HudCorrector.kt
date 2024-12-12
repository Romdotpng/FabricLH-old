package lavahack.client.features.subsystem.subsystems

import lavahack.client.features.hud.Huds
import lavahack.client.features.subsystem.SubSystem
import lavahack.client.utils.client.enums.HudAnchors
import lavahack.client.utils.client.interfaces.impl.screenListener

object HudCorrector : SubSystem(
    "Hud Corrector"
) {
    override fun init() {
        screenListener {
            for(anchor in HudAnchors.values()) {
                val draggables = anchor.draggables
                var height = 0f

                for(draggable in draggables) {
                    anchor.corrector(draggable, height)

                    height += draggable.h
                }
            }

            /*for(hud in Huds.huds) {
                val hitbox = hud.HITBOX
                val anchor = hitbox.bound

                if(anchor == HudAnchors.None) {

                }
            } */
        }
    }
}