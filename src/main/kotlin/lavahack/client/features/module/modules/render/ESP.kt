package lavahack.client.features.module.modules.render

import lavahack.client.features.module.Module
import lavahack.client.features.module.modules.render.esp.EntityESP
import lavahack.client.features.module.modules.render.esp.HoleESP
import lavahack.client.features.module.modules.render.esp.JumpCircle
import lavahack.client.features.module.modules.render.esp.SpawnsESP

/**
 * @author _kisman_
 * @since 14:05 of 28.07.2023
 */
@Module.Info(
    name = "ESP",
    description = "Highlights something around you",
    category = Module.Category.RENDER,
    visible = false,
    toggleable = false,
    state = true,
    properties = Module.Properties(
        bind = false,
        visible = false
    ),
    modules = [
        EntityESP::class,
        HoleESP::class,
        JumpCircle::class,
        SpawnsESP::class
    ]
)
class ESP : Module()