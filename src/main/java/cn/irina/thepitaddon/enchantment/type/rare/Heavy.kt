package cn.irina.thepitaddon.enchantment.type.rare

import cn.irina.thepitaddon.manager.PitManager
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.getPitProfile
import net.mizukilab.pit.parm.listener.ITickTask
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

/*
 * @Author ShanguanLinG
 * @Date 2025/9/12 10:10
 * @Description: 未测试
 */

@ArmorOnly
class Heavy : AbstractEnchantment(), ITickTask {
    override fun getEnchantName(): String {
        return "厚重"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "heavy"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.OP
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(level: Int): String {
        return "&7穿戴时自身最大生命值提升 ${level * 0.5 + 0.5}❤%"
    }

    override fun handle(enchantLevel: Int, player: Player) {
        val profile = player.getPitProfile()
        val item = player.inventory.leggings
        if (!PitManager.hasPitEnchant(item, "heavy")) return
        Bukkit.getPlayer(profile.playerUuid) ?: return
        profile.extraMaxHealth["heavy"] = enchantLevel * 0.5 + 0.5
    }

    override fun loopTick(enchantLevel: Int): Int {
        return 20
    }

    @EventHandler
    fun onExit(event: PlayerQuitEvent) {
        val profile = event.player.getPitProfile()
        profile.extraMaxHealth.remove("heavy")
    }
}