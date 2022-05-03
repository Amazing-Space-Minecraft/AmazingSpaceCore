package net.starlegacy.command.starship

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Optional
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import java.util.Locale
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.set
import kotlin.math.ln
import kotlin.math.roundToInt
import net.starlegacy.command.SLCommand
import net.starlegacy.database.schema.starships.Blueprint
import net.starlegacy.feature.space.Space
import net.starlegacy.feature.space.SpaceWorlds
import net.starlegacy.feature.starship.DeactivatedPlayerStarships
import net.starlegacy.feature.starship.PilotedStarships
import net.starlegacy.feature.starship.StarshipDestruction
import net.starlegacy.feature.starship.active.ActivePlayerStarship
import net.starlegacy.feature.starship.active.ActiveStarships
import net.starlegacy.feature.starship.control.StarshipControl
import net.starlegacy.feature.starship.control.StarshipCruising
import net.starlegacy.redis
import net.starlegacy.util.Vec3i
import net.starlegacy.util.action
import net.starlegacy.util.distance
import net.starlegacy.util.msg
import net.starlegacy.util.normalize
import net.starlegacy.util.randomInt
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object MiscStarshipCommands : SLCommand() {
	@CommandAlias("release")
	fun onRelease(sender: Player) {
		DeactivatedPlayerStarships.deactivateAsync(getStarshipPiloting(sender)) {
			sender msg "&bReleased ship"
		}
	}

	@CommandAlias("unpilot")
	fun onUnpilot(sender: Player) {
		val starship = getStarshipPiloting(sender)
		PilotedStarships.unpilot(starship)
		sender msg "&bUnpiloted ship, but left it activated"
	}

	@CommandAlias("stopriding")
	fun onStopRiding(sender: Player) {
		val starship = getStarshipRiding(sender)

		failIf(starship is ActivePlayerStarship && starship.pilot == sender) {
			"You can't stop riding if you're the pilot. Use /release or /unpilot."
		}

		starship.removePassenger(sender.uniqueId)
		sender action "&eStopped riding ship"
	}

	@CommandAlias("loadship")
	@CommandPermission("starships.loadship")
	@CommandCompletion("@players")
	fun onLoadShip(sender: Player, player: String, world: String) = asyncCommand(sender) {
		val uuid = resolveOfflinePlayer(player)
		redis {
			val key = "starships.lastpiloted.$uuid.${world.lowercase(Locale.getDefault())}"

			failIf(!exists(key)) { "$player doesn't have a ship saved for /loadship in $world" }

			val schematic = Blueprint.parseData(get(key))

			val pilotLoc = Vec3i(0, 0, 0)

			BlueprintCommand.checkObstruction(sender, schematic, pilotLoc)

			BlueprintCommand.loadSchematic(sender, schematic, pilotLoc)
		}
	}

	@CommandAlias("settarget|starget|st")
	fun onSetTarget(sender: Player, set: String, @Optional player: OnlinePlayer?) {
		val starship = getStarshipRiding(sender)
		val weaponSet = set.lowercase(Locale.getDefault())
		failIf(!starship.weaponSets.containsKey(weaponSet)) {
			"No such weapon set $weaponSet"
		}
	}

	@CommandAlias("powerdivision|powerd|pdivision|pd|powermode|pm")
	fun onPowerDivision(sender: Player, shield: Int, weapon: Int, thruster: Int) {
		val sum = shield + weapon + thruster
		val shieldPct = (shield.toDouble() / sum * 100.0).toInt()
		val weaponPct = (weapon.toDouble() / sum * 100.0).toInt()
		val thrusterPct = (thruster.toDouble() / sum * 100.0).toInt()

		failIf(arrayOf(shieldPct, weaponPct, thrusterPct).any { it !in 10..50 }) {
			"Power mode $shieldPct $weaponPct $thrusterPct is not allowed! None can be less than 10% or greater than 50%."
		}

		getStarshipRiding(sender).updatePower(sender, shieldPct, weaponPct, thrusterPct)
	}

	@CommandAlias("nukeship")
	@CommandPermission("starships.nukeship")
	fun onNukeShip(sender: Player) {
		val ship = getStarshipRiding(sender) as? ActivePlayerStarship ?: return
		StarshipDestruction.vanish(ship)
	}

	@CommandAlias("directcontrol|dc")
	fun onDirectControl(sender: Player) {
		val starship = getStarshipPiloting(sender)
		failIf(!starship.isDirectControlEnabled && !StarshipControl.isHoldingController(sender)) {
			"You need to hold a starship controller to enable direct control"
		}
		starship.setDirectControlEnabled(!starship.isDirectControlEnabled)
	}

	@CommandAlias("cruise")
	fun onCruise(sender: Player) {
		val ship = getStarshipPiloting(sender)
		if (!StarshipCruising.isCruising(ship)) {
			StarshipCruising.startCruising(sender, ship)
		} else {
			StarshipCruising.stopCruising(sender, ship)
		}
	}

	@CommandAlias("cruisespeed|csp|speedlimit|cruisespeedlimit")
	fun onCruiseSpeed(sender: Player, speedLimit: Int) {
		val ship = getStarshipPiloting(sender)
		ship.speedLimit = speedLimit
		sender msg "&3Speed limit set to $speedLimit"
	}

	@CommandAlias("eject")
	fun onEject(sender: Player, who: OnlinePlayer) {
		val starship = getStarshipPiloting(sender)
		val player = who.player
		failIf(sender == player) { "Can't eject yourself" }
		val inHitbox = starship.isWithinHitbox(player)
		starship.removePassenger(player.uniqueId)
		failIf(!inHitbox) { "${player.name} is not riding!" }
		val location = player.location
		val x = ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
		var y = ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
		val z = ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
		while (starship.isWithinHitbox(location)) {
			location.add(x, y, z)
			if (location.y < 5 || location.y > 250) {
				y *= -1
			}
		}
		player.teleport(location)
		starship.sendMessage("&c${player.name} was ejected from the starship")
		player msg "&cYou were ejected from the starship"
	}

	@CommandAlias("listships")
	@CommandPermission("starships.listships")
	fun onListShips(sender: Player) {
		var totalShips = 0
		var totalBlocks = 0

		for (starship in ActiveStarships.all()) {
			val pilot: Player? = (starship as? ActivePlayerStarship)?.pilot
			totalShips++
			val size: Int = starship.blockCount
			totalBlocks += size
			val typeName = starship.type.displayName
			val pilotName = pilot?.name ?: "none"
			val worldName = starship.world.name
			sender msg "$typeName piloted by $pilotName with block count $size in world $worldName"
		}

		sender msg "&7Total Ships&8:&b $totalShips&8"
		sender msg "&7Total Blocks in all ships&8:&b $totalBlocks"
	}
}
