package net.starlegacy.command.ship

import co.aikar.commands.annotation.CommandAlias
import kotlin.math.round
import kotlin.math.roundToInt
import net.starlegacy.command.SLCommand
import net.starlegacy.feature.starship.StarshipDetection
import net.starlegacy.util.Vec3i
import net.starlegacy.util.isWool
import net.starlegacy.util.msg
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object StarshipInfoCommand : SLCommand() {
	@CommandAlias("shipinfo|ship")
	fun onExecute(p: Player) {
		val ship = getStarshipPiloting(p)

		val blocks = ship.blocks.map { Vec3i(it) }.associateWith { it.toLocation(ship.world).block.state }

		val size = ship.blockCount

		p msg "&b${ship.data.type.displayName} &f($size blocks)"
		p msg "   &7Mass:&f ${ship.mass}"
		p msg "   &7World:&f ${ship.world.name}"
		p msg "   &7Pilot:&f ${ship.pilot?.name}"

		val passengers = ship.onlinePassengers.map { it.name }.joinToString()
		if (passengers.any()) {
			p msg "   &7Passengers:&f $passengers"
		}

		p msg "   &7Wool Percent:&f ${createPercent(blocks.values.count { it.type.isWool }, size)}"

		val inventoryCount = blocks.values.count { StarshipDetection.isInventory(it.type) } +
			blocks.values.count { it.type == Material.CHEST || it.type == Material.TRAPPED_CHEST } * 2
		p msg "   &7Inventory Percent:&f ${createPercent(inventoryCount, size)}"
		}
		if (!ship.weaponSets.isEmpty) {
			p msg "   &7Controlled Weapon Sets:"
			for (gunner in ship.weaponSetSelections.mapNotNull { Bukkit.getPlayer(it.key) }) {
				val weaponSet = ship.weaponSetSelections[gunner.uniqueId]
				p msg "         &6${gunner.name}:&c $weaponSet"
			}
		}

		p msg "   &7Ship Integrity:&f ${ship.hullIntegrity().times(100).roundToInt()}%"
		p msg "   &7Center of Mass:&f ${ship.centerOfMass}"

	}

	// creates a percent that goes down to the tens place
	private fun createPercent(numerator: Int, denominator: Int): String =
		createPercent(numerator.toDouble() / denominator.toDouble())

	private fun createPercent(fraction: Double) = "${round(fraction * 1000) / 10}%"
}
