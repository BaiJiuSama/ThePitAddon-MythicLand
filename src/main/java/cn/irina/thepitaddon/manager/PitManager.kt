package cn.irina.thepitaddon.manager

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import net.mizukilab.pit.util.item.ItemUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @Author ShanguanLinG
 * @Date 2025/8/07
 * @Assist Irina
 */

object PitManager {
    private val pitInstance = ThePit.getInstance()
    private val pitApi = ThePit.api
    val prefix = Main.instance.PREFIX

    @JvmStatic
    fun hasInternalName(item: ItemStack, internalName: String): Boolean {
        return item != null && internalName == ItemUtil.getInternalName(item)
    }

    @Throws(NullPointerException::class)
    fun getInternalName(item: ItemStack): String {
        return ItemUtil.getInternalName(item)
    }

    fun hasPitEnchant(item: ItemStack, enchantName: String): Boolean {
        return getPitEnchantLevel(item, enchantName) > 0
    }

    fun getPitEnchantLevel(item: ItemStack, enchantName: String): Int {
        return pitApi.getItemEnchantLevel(item, enchantName)
    }

    fun takeInterNalItem(player: Player, internalName: String, count: Int) {
        var remaining = count
        val inventory = player.inventory
        for (slot in 0 until inventory.size) {
            if (remaining <= 0) break
            val item = inventory.getItem(slot) ?: continue
            if (!hasInternalName(item, internalName)) continue
            val takeAmount = minOf(item.amount, remaining)
            if (item.amount > takeAmount) {
                item.amount -= takeAmount
                inventory.setItem(slot, item)
            } else {
                inventory.setItem(slot, null)
            }
            remaining -= takeAmount
        }
        player.updateInventory()
    }

    @Throws(NullPointerException::class)
    fun getInternalItemFromInventory(player: Player, internalName: String): ItemStack? {
        for (slot in 0 until player.inventory.size) {
            val item = player.inventory.getItem(slot) ?: continue
            if (!hasInternalName(item, internalName)) continue
            return item
        }
        return null
    }

    fun getInternalItemAmount(player: Player, internalName: String): Int {
        var amount = 0
        for (item in player.inventory) {
            if (item == null || item.type == Material.AIR) continue
            if (!hasInternalName(item, internalName)) continue
            amount += item.amount
        }
        return amount
    }

    fun isAmulet(item: ItemStack): Boolean {
        return getInternalName(item).startsWith("amulet_")
    }

    fun isAmulet(item: ItemStack, amuletName: String): Boolean {
        return getInternalName(item) == "amulet_$amuletName"
    }

    fun hasInternalItem(player: Player, internalName: String): Boolean {
        return getInternalItemAmount(player, internalName) > 0
    }

    fun hasEnoughInternalItem(player: Player, internalName: String, count: Int): Boolean {
        return getInternalItemAmount(player, internalName) >= count
    }
}
