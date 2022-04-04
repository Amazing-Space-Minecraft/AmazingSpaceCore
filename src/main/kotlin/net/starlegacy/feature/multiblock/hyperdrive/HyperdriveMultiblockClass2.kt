package net.starlegacy.feature.multiblock.hyperdrive

import net.starlegacy.feature.multiblock.MultiblockShape
import net.starlegacy.util.Vec3i

object HyperdriveMultiblockClass2 : HyperdriveMultiblock() {
	override val maxPower = 50_000

	override val signText = createSignText(
		line1 = "&7Class",
		line2 = "&12",
		line3 = "&bHyperdrive",
		line4 = null
	)

	override val hyperdriveClass = 2

	override fun MultiblockShape.buildStructure() {
		addHoppers(this)

		z(+0) {
			y(-1) {
				x(0).wireInputComputer()
			}

			for (y in 0..1) y(y) {
				x(-1).anyGlassPane()
				x(+0).sponge()
				x(+1).anyGlassPane()
			}
		}

		z(+1) {
			y(-1) {
				x(+0).diamondBlock()
			}

			for (y in 0..1) y(y) {
				x(-1).anyGlass()
				x(+0).diamondBlock()
				x(+1).anyGlass()
			}
		}

		z(+2) {
			for (y in 0..1) y(y) {
				x(-1).anyGlassPane()
				x(+0).anyGlass()
				x(+1).anyGlassPane()
			}
		}
	}

	override fun buildHopperOffsets() = listOf(
		Vec3i(x = -1, y = -1, z = +1),    // left hopper
		Vec3i(x = +1, y = -1, z = +1),    // right hopper
		Vec3i(x = +0, y = -1, z = +2)     // rear hopper
	)
}
