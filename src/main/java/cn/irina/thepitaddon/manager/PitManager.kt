package cn.irina.thepitaddon.manager

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import net.mizukilab.pit.util.item.ItemUtil
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

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
        return internalName == getInternalName(item)
    }

    @JvmStatic
    fun getInternalName(item: ItemStack): String {
        return ItemUtil.getInternalName(item) ?: ""
    }

    @JvmStatic
    fun hasPitEnchant(item: ItemStack, enchantName: String): Boolean {
        return getPitEnchantLevel(item, enchantName) > 0
    }

    @JvmStatic
    fun getPitEnchantLevel(item: ItemStack, enchantName: String): Int {
        return pitApi.getItemEnchantLevel(item, enchantName)
    }

    @JvmStatic
    fun getAbsorptionHearts(player: Player): Float {
        val craftPlayer = player as CraftPlayer
        return craftPlayer.handle.absorptionHearts
    }

    fun hasAbsolutionHearts(player: Player): Boolean {
        val absorptionHearts = getAbsorptionHearts(player)
        return absorptionHearts > 0
    }

    @JvmStatic
    fun takeInternalItem(player: Player, internalName: String, count: Int) {
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

    @JvmStatic
    fun getInternalItemAmount(player: Player, internalName: String): Int {
        var amount = 0
        for (item in player.inventory) {
            if (item == null || item.type == Material.AIR) continue
            if (!hasInternalName(item, internalName)) continue
            amount += item.amount
        }
        return amount
    }

    @JvmStatic
    fun givePlayerSpeedBuff(player: Player, duration: Int, level: Int) {
        val existingSpeed = player.activePotionEffects.find { it.type == PotionEffectType.SPEED }
        if (existingSpeed == null) {
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SPEED,
                    duration,
                    level,
                    true
                )
            )
        } else {
            if (existingSpeed.amplifier > level) return
            if (existingSpeed.amplifier >= level && existingSpeed.duration > duration) return
            player.removePotionEffect(PotionEffectType.SPEED)
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SPEED,
                    duration,
                    level,
                    true
                )
            )
        }
    }

    @JvmStatic
    fun isAmulet(item: ItemStack): Boolean {
        return getInternalName(item).startsWith("amulet_")
    }

    @JvmStatic
    fun isAmulet(item: ItemStack, amuletName: String): Boolean {
        return getInternalName(item) == "amulet_$amuletName"
    }

    @JvmStatic
    fun hasInternalItem(player: Player, internalName: String): Boolean {
        return getInternalItemAmount(player, internalName) > 0
    }

    @JvmStatic
    fun hasEnoughInternalItem(player: Player, internalName: String, count: Int): Boolean {
        return getInternalItemAmount(player, internalName) >= count
    }
}
