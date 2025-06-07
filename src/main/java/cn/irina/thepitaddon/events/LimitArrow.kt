package cn.irina.thepitaddon.events

import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent

class LimitArrow : Listener {
    @EventHandler
    fun onPlayerShootsArrow(event: EntityShootBowEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.hasPermission("pit.shoot")) {
                fillArrows(player)
            }
        }
    }

    private fun fillArrows(player: Player) {
        val hasCount = player.inventory.contents
            .filter { it != null && it.type == Material.ARROW }
            .sumOf { it.amount }

        val diff = 32 - hasCount
        if (diff != 0) {
            if (diff > 0) givePitArrows(player, diff)
            else removePitArrows(player, -diff)
        }
    }

    private fun givePitArrows(player: Player, count: Int) {
        player.inventory.addItem(createPitArrow(count).build())
    }

    private fun removePitArrows(player: Player, count: Int) {
        player.inventory.removeItem(createPitArrow(count).build())
    }

    private fun createPitArrow(amount: Int): ItemBuilder {
        return ItemBuilder(Material.ARROW)
            .internalName("default_arrow")
            .defaultItem()
            .canDrop(false)
            .canSaveToEnderChest(false)
            .amount(amount)
    }
}