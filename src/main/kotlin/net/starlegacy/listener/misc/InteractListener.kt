package net.starlegacy.listener.misc

import net.starlegacy.feature.misc.CustomBlockItem
import net.starlegacy.feature.misc.CustomBlocks
import net.starlegacy.feature.misc.CustomItems
import net.starlegacy.feature.multiblock.Multiblocks
import net.starlegacy.listener.SLEventListener
import net.starlegacy.util.LegacyBlockUtils
import net.starlegacy.util.Tasks
import net.starlegacy.util.axis
import net.starlegacy.util.colorize
import net.starlegacy.util.getFacing
import net.starlegacy.util.isStainedGlass
import net.starlegacy.util.isWallSign
import net.starlegacy.util.leftFace
import net.starlegacy.util.msg
import net.starlegacy.util.red
import net.starlegacy.util.rightFace
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Furnace
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object InteractListener : SLEventListener() {

	@EventHandler
	fun onBlockPlaceEvent(event: BlockPlaceEvent) {
		if (event.isCancelled) return
		if (!event.canBuild()) return

		val item = CustomItems[event.itemInHand] as? CustomBlockItem ?: return
		event.block.setBlockData(item.customBlock.blockData, false)
	}

	// When not in creative mode, make breaking a custom item drop the proper drops
	@EventHandler(priority = EventPriority.MONITOR)
	fun onBlockBreakEvent(event: BlockBreakEvent) {
		if (event.isCancelled) return
		if (event.player.gameMode == GameMode.CREATIVE) return

		val block = event.block
		val customBlock = CustomBlocks[block] ?: return

		if (event.isDropItems) {
			event.isDropItems = false
			block.type = Material.AIR
		}

		val itemUsed = event.player.inventory.itemInMainHand
		val location = block.location.toCenterLocation()
		Tasks.sync {
			for (drop in customBlock.getDrops(itemUsed)) {
				block.world.dropItem(location, drop)
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onBlockPlace(event: BlockPlaceEvent) {
		val player = event.player

		val hand = event.hand
		val itemStack = player.inventory.getItem(hand)?.clone() ?: return
		val item: CustomBlockItem = CustomItems[itemStack] as? CustomBlockItem ?: return

		event.block.location.block.setBlockData(item.customBlock.blockData, true)
	}
}
