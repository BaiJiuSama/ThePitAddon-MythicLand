package cn.irina.thepitaddon.events

import cn.charlotte.pit.data.PlayerProfile
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EXItemDamage : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onExItemDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player || event.damager !is Player) return

        val target = event.entity as Player
        val attacker = event.damager as Player

        val attackerItem = attacker.inventory.itemInHand

        if (attackerItem == null || !attackerItem.type.name.contains("DIAMOND")) return

        val targetProfile = PlayerProfile.getRawCache(target.uniqueId)

        if (targetProfile == null || targetProfile.bounty <= 0) return

        event.damage *= 1.3
    }
}

