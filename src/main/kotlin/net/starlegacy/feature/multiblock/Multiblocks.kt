package net.starlegacy.feature.multiblock

import co.aikar.timings.Timing
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.starlegacy.SLComponent
import net.starlegacy.feature.multiblock.misc.MagazineMultiblock
import net.starlegacy.feature.multiblock.starshipweapon.cannon.LaserCannonStarshipWeaponMultiblock
import net.starlegacy.feature.multiblock.starshipweapon.cannon.PlasmaCannonStarshipWeaponMultiblock
import net.starlegacy.feature.multiblock.starshipweapon.cannon.PulseCannonStarshipWeaponMultiblock
import net.starlegacy.util.msg
import net.starlegacy.util.time
import net.starlegacy.util.timing
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object Multiblocks : SLComponent() {
	private lateinit var multiblocks: List<Multiblock>

	private fun initMultiblocks() {
		multiblocks = listOf(
			MagazineMultiblock,

			LaserCannonStarshipWeaponMultiblock,
			PlasmaCannonStarshipWeaponMultiblock,
			PulseCannonStarshipWeaponMultiblock,
		)
	}

	private val multiblockCache: MutableMap<Location, Multiblock> = Object2ObjectOpenHashMap()

	private lateinit var gettingTiming: Timing
	private lateinit var detectionTiming: Timing

	override fun onEnable() {
		initMultiblocks()

		gettingTiming = timing("Multiblock Getting")
		detectionTiming = timing("Multiblock Detection")

		log.info("Loaded ${multiblocks.size} multiblocks")
	}

	fun all(): List<Multiblock> = multiblocks

	@JvmStatic
	@JvmOverloads
	operator fun get(
		sign: Sign, checkStructure: Boolean = true, loadChunks: Boolean = true
	): Multiblock? = gettingTiming.time {
		val location: Location = sign.location
		val lines: Array<String> = sign.lines

		val cached: Multiblock? = multiblockCache[location]
		if (cached != null) {
			// one was already cached before
			if (cached.matchesSign(lines) && (!checkStructure || cached.signMatchesStructure(sign, loadChunks))) {
				// it still matches so returned the cached one
				return@time cached
			} else {
				// it no longer matches so remove it, and re-detect it afterwards
				multiblockCache.remove(location)
			}
		}

		for (multiblock in multiblocks) {
			val matchesSign = multiblock.matchesSign(lines)
			if (matchesSign && (!checkStructure || multiblock.signMatchesStructure(sign, loadChunks))) {
				if (checkStructure) {
					multiblockCache[location] = multiblock
				}
				return@time multiblock
			}
		}

		return@time null
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	fun onInteractMultiblockSign(event: PlayerInteractEvent) {
		if (event.hand != EquipmentSlot.HAND || event.action != Action.RIGHT_CLICK_BLOCK) {
			return
		}

		val sign = event.clickedBlock?.state as? Sign ?: return
		var lastMatch: Multiblock? = null
		val player = event.player

		if (!player.hasPermission("starlegacy.multiblock.detect")) {
			player msg "&cYou don't have permission to detect multiblocks!"
			return
		}

		for (multiblock in multiblocks) {
			if (multiblock.matchesUndetectedSign(sign)) {
				if (multiblock.signMatchesStructure(sign, particles = true)) {
					return multiblock.setupSign(player, sign)
				} else {
					lastMatch = multiblock
				}
			}
		}

		if (lastMatch != null) {
			player msg "&4Improperly built &c${lastMatch.name}&4. Make sure every block is correctly placed!"
		}
	}
}
