package cn.irina.thepitaddon.utils

import cn.charlotte.pit.util.item.ItemBuilder
import cn.charlotte.pit.util.item.ItemUtil
import org.bukkit.Material
import org.bukkit.entity.Player

object InvUtil {
    fun hasEmptySlot(player: Player, slots: Int): Boolean {
        var emptySlotCount = 0
        var slotCount = 0
        while (slotCount < 36) {
            if (player.inventory.getItem(slotCount) == null || player.inventory.getItem(slotCount).type == Material.AIR) {
                ++emptySlotCount
                if (emptySlotCount >= slots) {
                    return true
                }
            }
            ++slotCount
        }

        return false
    }

    fun hasEnoughItemCount(player: Player, itemInternalName: String?, count: Int, material: Material): Boolean {
        var itemCounts = 0
        for (item in 0..36) {
            if (player.inventory.getItem(item) != null && player.inventory.getItem(item).type == material && ItemUtil.getInternalName(
                    player.inventory.getItem(item)
                ).equals(itemInternalName, ignoreCase = true)
            ) {
                val meta = player.inventory.getItem(item).itemMeta
                if (!meta.hasEnchants()) {
                    itemCounts++
                }
            }
        }

        return itemCounts >= count
    }

    fun removeEnoughItemCount(player: Player, itemInternalName: String?, count: Int, material: Material) {
        var itemCount = 0
        for (item in 0..36) {
            if (player.inventory.getItem(item) != null && player.inventory.getItem(item).type == material && ItemUtil.getInternalName(player.inventory.getItem(item)).equals(itemInternalName, ignoreCase = true)) {
                val meta = player.inventory.getItem(item).itemMeta
                if (!meta.hasEnchants()) return
                itemCount++
                if (itemCount <= count) {
                    player.inventory.setItem(item, ItemBuilder(Material.AIR).build())
                } else {
                    return
                }

            }
        }
    }
}
