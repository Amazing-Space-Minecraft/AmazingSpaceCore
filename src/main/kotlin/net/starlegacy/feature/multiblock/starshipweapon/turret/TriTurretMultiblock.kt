package net.starlegacy.feature.multiblock.starshipweapon.turret

import java.util.concurrent.TimeUnit
import net.starlegacy.feature.multiblock.MultiblockShape
import net.starlegacy.feature.starship.active.ActiveStarship
import net.starlegacy.feature.starship.subsystem.weapon.TurretWeaponSubsystem
import net.starlegacy.feature.starship.subsystem.weapon.secondary.TriTurretWeaponSubsystem
import net.starlegacy.util.Vec3i
import org.bukkit.Material.GRINDSTONE
import org.bukkit.Material.IRON_TRAPDOOR
import org.bukkit.block.BlockFace

sealed class TriTurretMultiblock : TurretMultiblock() {
	override fun createSubsystem(starship: ActiveStarship, pos: Vec3i, face: BlockFace): TurretWeaponSubsystem {
		return TriTurretWeaponSubsystem(starship, pos, getFacing(pos, starship), this)
	}

	protected abstract fun getYFactor(): Int

	override val cooldownNanos: Long = TimeUnit.SECONDS.toNanos(3L)
	override val range: Double = 500.0
	override val sound: String = "starship.weapon.turbolaser.tri.shoot"

	override val projectileSpeed: Int = 125
	override val projectileParticleThickness: Double = 0.8
	override val projectileExplosionPower: Float = 6f
	override val projectileShieldDamageMultiplier: Int = 3

	override fun buildFirePointOffsets(): List<Vec3i> = listOf(
		Vec3i(-2, getYFactor() * 3, +6),
		Vec3i(+0, getYFactor() * 3, +7),
		Vec3i(+2, getYFactor() * 3, +6)
	)

	override fun MultiblockShape.buildStructure() {
		y(getYFactor() * 2) {
			z(-1) {
				x(-1).sponge()
				x(+0).sponge()
				x(+1).sponge()
			}

			z(+0) {
				x(-1).sponge()
				x(+1).sponge()
			}

			z(+1) {
				x(-1).sponge()
				x(+0).sponge()
				x(+1).sponge()
			}
		}

		y(getYFactor() * 3) {
			z(-3) {
				x(-1).anyStairs()
				x(+0).stainedTerracotta()
				x(+1).anyStairs()
			}

			z(-2) {
				x(-2..+2) { stainedTerracotta() }
			}

			z(-1) {
				x(-3).anyStairs()
				x(-2..+2) { stainedTerracotta() }
				x(+3).anyStairs()
			}

			z(+0) {
				x(-3..+3) { stainedTerracotta() }
			}

			z(+1) {
				x(-3).anyStairs()
				x(-2..+2) { stainedTerracotta() }
				x(+3).anyStairs()
			}

			z(+2) {
				x(-2..+2) { stainedTerracotta() }
			}

			z(+3) {
				x(-1).anyStairs()
				x(+0).stainedTerracotta()
				x(+1).anyStairs()
			}
		}

		y(getYFactor() * 4) {
			z(-3) {
				x(+0).anyStairs()
			}

			z(-2) {
				x(-2).type(IRON_TRAPDOOR)
				x(-1).anyStairs()
				x(+0).ironBlock()
				x(+1).anyStairs()
				x(+2).type(IRON_TRAPDOOR)
			}

			z(-1) {
				x(-2).anyStairs()
				x(-1).ironBlock()
				x(+0).ironBlock()
				x(+1).ironBlock()
				x(+2).anyStairs()
			}

			z(+0) {
				x(-3).anySlab()
				x(-2).type(GRINDSTONE)
				x(-1).anyStairs()
				x(+0).ironBlock()
				x(+1).anyStairs()
				x(+2).type(GRINDSTONE)
				x(+3).anySlab()
			}

			z(+1) {
				x(-2).type(LIGHTNING_ROD)
				x(-1).anySlab()
				x(+0).type(GRINDSTONE)
				x(+1).anySlab()
				x(+2).type(LIGHTNING_ROD)
			}

			z(+2) {
				x(-2).type(LIGHTNING_ROD)
				x(-1).type(IRON_TRAPDOOR)
				x(+0).etype(LIGHTNING_ROD)
				x(+1).type(IRON_TRAPDOOR)
				x(+2).type(LIGHTNING_ROD)
			}

			z(+3) {
				x(+0).type(LIGHTNING_ROD)
			}
		}
	}
}

object TopTriTurretMultiblock : TriTurretMultiblock() {
	override fun getYFactor(): Int = 1
	override fun getPilotOffset(): Vec3i = Vec3i(+0, +3, +2)
}

object BottomTriTurretMultiblock : TriTurretMultiblock() {
	override fun getYFactor(): Int = -1
	override fun getPilotOffset(): Vec3i = Vec3i(+0, -4, +2)
}
