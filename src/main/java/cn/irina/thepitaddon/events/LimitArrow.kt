package cn.irina.thepitaddon.events

import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class LimitArrow : Listener {
    @EventHandler
    fun onPlayerShootsArrow(cnm: PlayerInteractEvent) {
        val cnm1 = cnm.player
        if (cnm1.hasPermission("pit.shoot") &&
            cnm.action != null && cnm.action.name.contains("LEFT_CLICK") &&
            cnm1.itemInHand.type == Material.GOLD_SWORD
        ) {
            cnm2(cnm1)
        }
    }

    private fun cnm2(cnm3: Player) {
        val cnm4 = cnm3.inventory.contents
            .filter { it != null && it.type == Material.ARROW }
            .sumOf { it.amount }

        if (cnm4 < 32) cnm5(cnm3, 32 - cnm4)
        if (cnm4 > 32) cnm6(cnm3, cnm4 - 32)
    }

    private fun cnm5(cnm7: Player, cnm8: Int) {
        cnm7.inventory.addItem(cnm9(cnm8).build())
    }

    private fun cnm6(cnm10: Player, cnm11: Int) {
        cnm10.inventory.removeItem(cnm9(cnm11).build())
    }

    private fun cnm9(cnm12: Int): ItemBuilder {
        return ItemBuilder(Material.ARROW)
            .internalName("default_arrow")
            .defaultItem()
            .canDrop(false)
            .canSaveToEnderChest(false)
            .amount(cnm12)
    }
}