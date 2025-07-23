package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.FixListeners
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerDamaged
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.atomic.AtomicBoolean

@ArmorOnly
class Rationalist : AbstractEnchantment(), IPlayerDamaged {
    override fun getEnchantName(): String {
        return "理性主义者"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "rationalist"
    }

    override fun getRarity(): EnchantmentRarity {
        //return EnchantmentRarity.RAGE_RARE
        return EnchantmentRarity.OP
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "受到的伤害 &9-${4 + (enchantLevel * 2)}% /s" +
                "&7同时, 受击时若敌方手持武器含有 &d赌徒 &7附魔 /s" +
                "&7自身获得 &c生命恢复 " + RomanUtil.convert(2 + enchantLevel) + " &f(00:01) /s" +
                "&7并获得 &6${0.5 + (enchantLevel * 0.5)}❤ 生命吸收"
    }

    override fun handlePlayerDamaged(
        enchantLevel: Int,
        victim: Player,
        entity: Entity,
        v: Double,
        atomicDouble: AtomicDouble,
        boostDamage: AtomicDouble,
        atomicBoolean: AtomicBoolean
    ) {
        if (entity !is Player) return
        boostDamage.set(boostDamage.get() * (1 + (0.04 + (enchantLevel * 0.02))))
        if (ThePit.getApi().getItemEnchantLevel(entity.itemInHand, "gamble_enchant") <= 0) return
        if (victim.hasPotionEffect(PotionEffectType.REGENERATION)) victim.removePotionEffect(PotionEffectType.REGENERATION)
        victim.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20, 1 + enchantLevel, false, true))

        val craftPlayer = victim as CraftPlayer
        val absorptionHearts = craftPlayer.handle.absorptionHearts
        if (absorptionHearts >= FixListeners.LimitAbsorptionHearts) return
        craftPlayer.handle.absorptionHearts += 2 * (0.4 + (0.4 * enchantLevel)).toFloat()
    }
}
