package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.buff.impl.CoagulationBuff
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.WeaponOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IAttackEntity
import net.mizukilab.pit.util.cooldown.Cooldown
import cn.irina.thepitaddon.utils.TimeUtil
import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

@WeaponOnly
class Stifle : AbstractEnchantment(), IAttackEntity, IActionDisplayEnchant {
    private val cooldown: ConcurrentHashMap<UUID, Cooldown> = ConcurrentHashMap()
    private val pitAPI = ThePit.api

    override fun getEnchantName(): String {
        return "扼杀"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "stifle"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        // "&7攻击时造成的伤害将 &c-${(10 + (enchantLevel * 5))}% /s" +
        return "&7但会对目标施加效果 &4凝血 &f(${TimeUtil.formatTotalSeconds(if (enchantLevel >= 3) 4 else 2)}) &7(${28 - (enchantLevel * 4)}s冷却) /s" +
                "&7效果 &4凝血 &7: 无法恢复生命值"
    }

    override fun handleAttackEntity(
        enchantLevel: Int,
        player: Player,
        entity: Entity,
        v: Double,
        atomicDouble: AtomicDouble,
        boostDamage: AtomicDouble,
        atomicBoolean: AtomicBoolean
    ) {
        val target = entity as? Player ?: return

        if (pitAPI.getItemEnchantLevel(target.inventory.leggings, "control") >= 1 || !cooldown.getOrDefault(player.uniqueId, Cooldown(0L)).hasExpired()) return
        cooldown[player.uniqueId] = Cooldown((24 - (enchantLevel * 4)).toLong(), TimeUnit.SECONDS)

//        boostDamage.set(boostDamage.get() * (1 - (0.1 + (enchantLevel * 0.05))))
        CoagulationBuff().stackBuff(target, if (enchantLevel >= 3) 4000 else 2000)
        if (target.hasPotionEffect(PotionEffectType.WITHER)) target.removePotionEffect(PotionEffectType.WITHER)
        target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, (if (enchantLevel >= 2) 2 else 1) * 20, 0, false, true))
    }

    override fun getText(p0: Int, p1: Player): String? {
        return getCooldownActionText(cooldown.getOrDefault(p1.uniqueId, Cooldown(0L)))
    }
}
