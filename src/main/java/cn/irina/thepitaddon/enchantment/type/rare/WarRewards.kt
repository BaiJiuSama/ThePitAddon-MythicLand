package cn.irina.thepitaddon.enchantment.type.rare

import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerKilledEntity
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class WarRewards : AbstractEnchantment(), IPlayerKilledEntity {
    override fun getEnchantName(): String {
        return "战酬"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "war_rewards"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RAGE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7击杀获得 &6+" + (30 + (enchantLevel * 20)) + "% &7硬币"
    }

    override fun handlePlayerKilled(
        enchantLevel: Int,
        killer: Player,
        entity: Entity,
        coins: AtomicDouble,
        experience: AtomicDouble
    ) {
        val boost = enchantLevel * 0.2 + 0.3 + 1.0
        coins.set(coins.get() * boost)
    }
}