package net.starlegacy.listener.misc

import net.starlegacy.listener.SLEventListener
import net.starlegacy.util.stripColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.world.StructureGrowEvent

object BlockListener : SLEventListener() {
	// Disable block physics for portals to prevent airlocks from breaking
	@EventHandler
	fun onBlockPhysicsEvent(event: BlockPhysicsEvent) {
		if (event.block.type != Material.END_PORTAL) return
		event.isCancelled = true
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	fun onSignChange(event: SignChangeEvent) {
		for (i in 0 until 4) {
			event.setLine(i, event.getLine(i)?.stripColor())
		}
	}
}
