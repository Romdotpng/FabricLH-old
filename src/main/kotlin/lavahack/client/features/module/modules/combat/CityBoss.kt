package lavahack.client.features.module.modules.combat

import lavahack.client.features.module.Module
import lavahack.client.settings.Setting
import lavahack.client.settings.types.SettingGroup
import lavahack.client.utils.client.enums.Protocols
import lavahack.client.utils.client.interfaces.impl.prefix
import lavahack.client.utils.client.interfaces.impl.register
import lavahack.client.utils.client.interfaces.impl.tickListener
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

@Module.Info(
    name = "CityBoss",
    description = "Automatically breaks surround of enemy",
    category = Module.Category.WIP
)
class CityBoss : Module() {
    init {
        val casesGroup = register(SettingGroup("Cases"))
        val caseStates = mutableMapOf<Cases, Setting<Boolean>>()

        for(case in Cases.values()) {
            val setting = register(casesGroup.add(Setting(case.name, true)))

            caseStates[case] = setting
        }

        casesGroup.prefix("Cases")

        tickListener {
            if(mc.player == null || mc.world == null || enemy == null) {
                return@tickListener
            }

            
        }
    }

    class WrappedBlockPos(
        original : BlockPos,
        val protocol : Protocols = Protocols.New,
        val surround : Boolean = false,
        val base : Boolean = false
    ) : BlockPos(
        original
    )

    enum class Cases(
        val posses : (Direction) -> List<BlockPos>
    ) {
        Middle({
            listOf(
                WrappedBlockPos(BlockPos.ORIGIN.offset(it), surround = true),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).offset(it)),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).offset(it).up(), protocol = Protocols.Old),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).offset(it).down(), base = true)
            )
        }),
        Simple1({
            listOf(
                WrappedBlockPos(BlockPos.ORIGIN.offset(it), surround = true),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).up(), protocol = Protocols.Old),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).down(), base = true)
            )
        }),
        Simple2({
            listOf(
                WrappedBlockPos(BlockPos.ORIGIN.offset(it), surround = true),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).offset(it)),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).offset(it).offset(it)),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).offset(it).offset(it).up(), protocol = Protocols.Old),
                WrappedBlockPos(BlockPos.ORIGIN.offset(it).offset(it).offset(it).down(), base = true)
            )
        })
    }
}