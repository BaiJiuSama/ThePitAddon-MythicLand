package cn.irina.thepitaddon.events

import cn.charlotte.pit.data.PlayerProfile
import cn.irina.thepitaddon.utils.InvUtil
import net.mizukilab.pit.util.item.ItemUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class CoinEvent: Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!event.action.name.startsWith("RIGHT")) return
        val player = event.player

        val handItem = player.itemInHand
        if (handItem.type != Material.GOLD_INGOT || !ItemUtil.getInternalName(handItem).equals("coin")) return

        val handItemName = ChatColor.stripColor(handItem.itemMeta.displayName)
        val money = when {
            handItemName.contains("一千") -> 1000
            handItemName.contains("一万") -> 10000
            handItemName.contains("十万") -> 100000
            handItemName.contains("一百万") -> 1000000
            handItemName.contains("一千万") -> 10000000
            else -> return
        }

        PlayerProfile.getRawCache(player.uniqueId).coins += money

        InvUtil.takeItemInHand(player)
    }
}