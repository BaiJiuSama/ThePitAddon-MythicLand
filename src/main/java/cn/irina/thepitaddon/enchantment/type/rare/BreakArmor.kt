package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.IActionDisplayEnchant
import cn.charlotte.pit.enchantment.param.item.BowOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.parm.listener.IPlayerShootEntity
import cn.charlotte.pit.util.PlayerUtil
import cn.charlotte.pit.util.cooldown.Cooldown
import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@BowOnly
class BreakArmor : AbstractEnchantment(), IPlayerShootEntity,  IActionDisplayEnchant {
    private val cooldown = HashMap<UUID, Cooldown>()

    override fun getEnchantName(): String {
        return "破甲"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "break_armor"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7箭矢造成伤害前首先造成 &c" + (enchantLevel * 0.5) + "❤ &7的&c必中&7伤害 &7(1s冷却)"
    }

    override fun handleShootEntity(
        enchantLevel: Int,
        attacker: Player,
        target: Entity,
        damage: Double,
        finalDamage: AtomicDouble,
        boostDamage: AtomicDouble,
        cancel: AtomicBoolean
    ) {
        if (!cooldown.getOrDefault(attacker.uniqueId, Cooldown(0L)).hasExpired()) return
        cooldown[attacker.uniqueId] = Cooldown(1L, TimeUnit.SECONDS)

        val targetPlayer = target as? Player ?: return
        PlayerUtil.damage(targetPlayer, PlayerUtil.DamageType.TRUE, enchantLevel.toDouble(), false)
    }

    override fun getText(i: Int, player: Player): String {
        return getCooldownActionText(cooldown.getOrDefault(player.uniqueId, Cooldown(0L)))
    }
}
