package net.starlegacy.feature.multiblock.reactors

import net.starlegacy.feature.multiblock.FurnaceMultiblock
import net.starlegacy.feature.multiblock.Multiblock
import net.starlegacy.feature.multiblock.MultiblockShape
import net.starlegacy.feature.multiblock.PowerStoringMultiblock
import net.starlegacy.feature.starship.active.ActiveStarship

// I still need to work on this, just formatting stuff out for now
abstract class ReactorMultiblock: Multiblock() {
	override val name: String
		get() = TODO("Not yet implemented")
	override val signText: List<String>
		get() = TODO("Not yet implemented")

	override fun MultiblockShape.buildStructure() {
		TODO("Not yet implemented")
	}
}
