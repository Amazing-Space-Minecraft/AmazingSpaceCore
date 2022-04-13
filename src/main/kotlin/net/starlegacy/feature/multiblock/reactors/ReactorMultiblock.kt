package net.starlegacy.feature.multiblock.reactors

import net.starlegacy.feature.multiblock.FurnaceMultiblock
import net.starlegacy.feature.multiblock.PowerStoringMultiblock

// I still need to work on this, just formatting stuff out for now
abstract class ReactorMultiblock : PowerStoringMultiblock(), FurnaceMultiblock {
	override val name: String = "reactor"
}
