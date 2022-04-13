package net.starlegacy.feature.multiblock.hullfactory

import net.starlegacy.feature.machine.PowerMachines
import net.starlegacy.feature.multiblock.FurnaceMultiblock
import net.starlegacy.feature.multiblock.Multiblock
import net.starlegacy.feature.multiblock.MultiblockShape
import net.starlegacy.feature.multiblock.PowerStoringMultiblock
import net.starlegacy.util.getFacing
import org.bukkit.Material
import org.bukkit.block.Furnace
import org.bukkit.block.Sign
import org.bukkit.event.inventory.FurnaceBurnEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
abstract class HullFactory: Multiblock() {
    override val name = "hullfactory"

    override val signText = createSignText(
        line1 = "Hull Factory",
        line2 = "&8-------------",
        line3 = null,
        line4 = "Chromatic Inc"
    )

    override val maxPower: Int = 50_000
    abstract fun getOutput(product: Material): ItemStack

    protected abstract fun MultiblockShape.RequirementBuilder.hullfactoryProductBlock()
    override fun MultiblockShape.RequirementBuilder.hullfactoryProductBlock() = hullBlock()
    hullBlock = (type(
        STONE_BRICKS,MOSSY_STONE_BRICKS,QUARTZ_BLOCK,SMOOTH_QUARTZ,NETHER_BRICK,RED_NETHER_BRICKS,BLACKSTONE,POLISHED_BLACKSTONE,POLISHED_BLACKSTONE_BRICKS,
        ANDESITE,POLISHED_ANDESITE,DIORITE,POLISHED_DIORITE,GRANITE,POLISHED_GRANITE,PRISMARINE,PRISMARINE_BRICKS,DARK_PRISMARINE,
        SANDSTONE,SMOOTH_SANDSTONE,RED_SANDSTONE,SMOOTH_RED_SANDSTONE,COBBLED_DEEPSLATE,POLISHED_DEEPSLATE,DEEPSLATE_BRICKS,DEEPSLATE_TILES,SMOOTH_STONE,
        COBBLESTONE,MOSSY_COBBLESTONE,BRICKS,END_STONE_BRICKS,PURPUR_BLOCK
    ))

    override fun MultiblockShape.buildStructure() {
        z(+0) {
            y(-1) {
                x(-1).ironBlock()
                x(+0).wireInputComputer()
                x(+1).ironBlock()
            }

            y(+0) {
                x(-1).anyGlassPane()
                x(+0).machineFurnace()
                x(+1).anyGlassPane()
            }
        }

        z(+1) {
            y(-1) {
                x(-1).ironBlock()
                x(+0).sponge()
                x(+1).ironBlock()
            }

            y(+0) {
                x(-1).anyGlass()
                x(+0).type(GRINDSTONE)
                x(+1).anyGlass()
            }
        }

        z(+2) {
            y(-1) {
                x(-1).anyGlass()
                x(+0).type(STONECUTTER)
                x(+1).anyGlass()
            }

            y(+0) {
                x(-1).anyGlass()
                x(+0).hullfactoryProductBlock()
                x(+1).anyGlass()
            }
        }

        z(+3) {
            y(-1) {
                x(-1).ironBlock()
                x(+0).sponge()
                x(+1).ironBlock()
            }

            y(+0) {
                x(-1).anyGlass()
                x(+0).endRod()
                x(+1).anyGlass()
            }
        }

        z(+4) {
            y(-1) {
                x(-1).ironBlock()
                x(+0).hopper()
                x(+1).ironBlock()
            }

            y(+0) {
                x(-1).anyGlassPane()
                x(+0).anyPipedInventory()
                X(+1).anyGlassPane()
            }
        }
    }

    override fun onFurnaceTick(
		event: FurnaceBurnEvent,
		furnace: Furnace,
		sign: Sign
    ) {
        event.isCancelled = true
		val smelting = furnace.inventory.smelting
		val fuel = furnace.inventory.fuel

        if (PowerMachines.getPower(sign) == 0
			|| smelting == null
			|| smelting.type != Material.PRISMARINE_CRYSTALS
			|| fuel == null
			|| fuel.type != Material.STONE // CHANGED FROM COBBLESTONE TO NORMAL STONE, if it doesnt work this might be why. Change it back to cobblestone if it doesnt work and check if it still doesnt work
		) return

        event.isBurning = false
		event.burnTime = 100
		furnace.cookTime = (-1000).toShort()
		event.isCancelled = false

        val direction = sign.getFacing().oppositeFace

		val state = sign.block.getRelative(direction, 5).getState(false)
			as? InventoryHolder ?: return


        val product = sign.block.getRelative(sign.getFacing().oppositeFace, 3).type
        val output = getOutput(product)

        val inventory = state.inventory
		if (!LegacyItemUtils.canFit(inventory, output)) {
			return
		}

        LegacyItemUtils.addToInventory(inventory, output)
		fuel.amount = fuel.amount - 1
		PowerMachines.removePower(sign, 250)
    }
