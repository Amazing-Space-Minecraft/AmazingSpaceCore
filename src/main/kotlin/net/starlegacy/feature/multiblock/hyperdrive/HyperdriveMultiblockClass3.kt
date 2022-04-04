package net.starlegacy.feature.multiblock.hyperdrive

import net.starlegacy.feature.multiblock.MultiblockShape
import net.starlegacy.util.Vec3i

object HyperdriveMultiblockClass3 : HyperdriveMultiblock() {
	override val maxPower = 75_000

	override val signText = createSignText(
		line1 = "&7Class",
		line2 = "&23",
		line3 = "&bHyperdrive",
		line4 = null
	)

	override val hyperdriveClass = 3

	override fun MultiblockShape.buildStructure() {
		addHoppers(this)

		z(+0) {
			y(-1) {
				x(0).wireInputComputer()
			}

			for (y in 0..1) y(y) {
				x(-2).anyGlassPane()
				x(-1).anyGlass()
				x(+0).sponge()
				x(+1).anyGlass()
				x(+2).anyGlassPane()
			}
		}

		z(+1) {
			y(-1) {
				x(-2).ironBlock()
				x(-1).diamondBlock()
				x(+0).ironBlock()
				x(+1).diamondBlock()
				x(+2).ironblock()
			}

			for (y in 0..1) y(y) {
				x(-2).sponge()
				x(-1).diamondBlock()
				x(+0).ironBlock()
				x(+1).diamondBlock()
				x(+2).sponge()
			}
		}

		z(+2) {
			y(-1) {
				x(0).ironBlock()
			}

			for (y in 0..1) y(y) {
				x(-2).anyGlassPane()
				x(-1).anyGlass()
				x(+0).sponge()
				x(+1).anyGlass()
				x(+2).anyGlassPane()
			}
		}
	}

	override fun buildHopperOffsets() = listOf(
		Vec3i(x = -1, y = -1, z = +0),    // front left hopper
		Vec3i(x = +1, y = -1, z = +0),    // front right hopper
		Vec3i(x = -1, y = -1, z = +2),    // rear left hopper
		Vec3i(x = +1, y = -1, z = +2)     // rear left hopper
	)
}
