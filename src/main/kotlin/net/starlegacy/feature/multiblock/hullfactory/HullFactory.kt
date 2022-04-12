package net.starlegacy.feature.multiblock.printer

import net.starlegacy.feature.machine.PowerMachines
import net.starlegacy.feature.multiblock.FurnaceMultiblock
import net.starlegacy.feature.multiblock.MultiblockShape
import net.starlegacy.feature.multiblock.PowerStoringMultiblock
import net.starlegacy.util.getFacing
import org.bukkit.Material
import org.bukkit.block.Furnace
import org.bukkit.block.Sign
import org.bukkit.event.inventory.FurnaceBurnEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

	override val name = "hullfactory"

	override val signText = createSignText(
		line1 = "Hull Factory",
		line2 = "&4-------------",
		line3 = null,
		line4 = "Chromatic Inc"
	)
