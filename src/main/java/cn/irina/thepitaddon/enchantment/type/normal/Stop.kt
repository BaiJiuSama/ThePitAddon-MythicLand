package cn.irina.thepitaddon.enchantment.type.normal

import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.BowOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerDamaged
import net.mizukilab.pit.parm.listener.IPlayerShootEntity
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

@BowOnly
class Stop : AbstractEnchantment(), IPlayerShootEntity, IPlayerDamaged {
    companion object {
        private val cooldowns = mutableMapOf<UUID, Cooldown>()
    }

    override fun getEnchantName(): String {
        return "停下!"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "please_stop"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.NORMAL
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        val duration = enchantLevel + 1
        val s = StringBuilder("&7弓箭命中敌人后对敌人施加 &c缓慢 I &f(00:0${duration}) /s")
        if (enchantLevel >= 2) s.append("&7持有此附魔被击中后, 若对方拥有 &c缓慢 &7效果, 则为自身恢复 &c${enchantLevel * 0.5 - 0.5}❤ &7(1s冷却)")
        return s.toString()
    }

    override fun handleShootEntity(
        enchantLevel: Int,
        attacker: Player,
        target: Entity?,
        damage: Double,
        finalDamage: AtomicDouble?,
        boostDamage: AtomicDouble?,
        cancel: AtomicBoolean?
    ) {
        if (target !is Player) return
        if (target.activePotionEffects.find { it.type == PotionEffectType.SLOW } != null)
            target.removePotionEffect(
                PotionEffectType.SLOW
            )
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, (enchantLevel + 1) * 20, 0))
    }

    override fun handlePlayerDamaged(
        enchantLevel: Int,
        myself: Player,
        attacker: Entity,
        damage: Double,
        finalDamage: AtomicDouble?,
        boostDamage: AtomicDouble?,
        cancel: AtomicBoolean?
    ) {
        if (enchantLevel < 2) return
        if (attacker !is Arrow) return
        val shooter = attacker.shooter
        if (shooter !is Player) return
        if (shooter.activePotionEffects.any { it.type == PotionEffectType.SLOW }) {
            val cooldown = cooldowns[myself.uniqueId]
            if (cooldown == null || cooldown.hasExpired()) {
                cooldowns[myself.uniqueId] = Cooldown(1000)
                val newHeal: Double = myself.health + (0.5 * enchantLevel - 0.5) * 2
                myself.health = min(newHeal, myself.maxHealth)
            }
        }
    }
}