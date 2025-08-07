package cn.irina.thepitaddon

import cn.charlotte.pit.ThePit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PitManager {

    /**
     * @Author ShanguanLinG
     * @Date 2025/8/07
     */

    companion object {
        val prefix = Main.instance.PREFIX

        @JvmStatic
        fun hasInternalName(item: ItemStack, internalName: String): Boolean {
            val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(item)
            return pitItem.internalName.equals(internalName)
        }

        fun hasPitEnchant(item: ItemStack, enchantName: String): Boolean {
            return getPitEnchantLevel(item, enchantName) > 0
        }

        fun getPitEnchantLevel(item: ItemStack, enchantName: String): Int {
            return ThePit.getApi().getItemEnchantLevel(item, enchantName)
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
            val inventory = player.inventory
            for (slot in 0 until inventory.size) {
                val item = inventory.getItem(slot) ?: continue
                if (!hasInternalName(item, internalName)) continue
                amount += item.amount
            }
            return amount
        }

        fun hasEnoughInternalItem(player: Player, internalName: String, count: Int): Boolean {
            return getInternalItemAmount(player, internalName) >= count
        }
    }
}