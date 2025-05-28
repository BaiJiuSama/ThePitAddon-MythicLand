package cn.irina.thepitaddon.events

import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class LimitArrow: Listener {
    private val pitArrow: ItemStack by lazy {
        ItemBuilder(Material.ARROW)
        .internalName("default_arrow")
        .defaultItem()
        .canDrop(false)
        .canSaveToEnderChest(false)
        .build()
    }

    @EventHandler
    fun onShoot(event: EntityShootBowEvent) {
        val player = event.entity as? Player ?: return
        player.inventory.addItem(pitArrow)
    }

    private val pitArrowClone by lazy {
        val arrow = pitArrow.clone()
        arrow.amount = 30
        arrow
    }
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        for (its in player.inventory) {
            if (its.type != Material.ARROW) continue
            its.type = Material.AIR
        }

        player.inventory.setItem(0, pitArrowClone)
    }
}