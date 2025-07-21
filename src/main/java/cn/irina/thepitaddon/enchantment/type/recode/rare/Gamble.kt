package cn.irina.thepitaddon.enchantment.type.recode.rare

import cn.irina.thepitaddon.utils.RandomUtil
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.WeaponOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IAttackEntity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicBoolean

@WeaponOnly
class Gamble : AbstractEnchantment(), IAttackEntity {
    override fun getEnchantName(): String {
        return "赌徒"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "gamble_enchant"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE;
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7攻击时有 &e100% &7的几率对自身或敌人 /s" +
                "&7额外造成 &c${getDamage(enchantLevel) / 2}❤ &7的&c必中&7伤害 /s" +
                "&c(必中伤害无法被免疫与抵抗)"
    }

    private fun getDamage(enchantLevel: Int): Double {
        return 1.0 + enchantLevel.toDouble()
    }

    override fun handleAttackEntity(
        enchantLevel: Int,
        attacker: Player,
        entity: Entity,
        p3: Double,
        p4: AtomicDouble?,
        p5: AtomicDouble?,
        p6: AtomicBoolean?
    ) {
        if (RandomUtil().hasSuccessfullyByChance(0.5)) {
            PlayerUtil.damage(attacker, PlayerUtil.DamageType.TRUE, getDamage(enchantLevel), false)
            attacker.sendMessage(CC.translate("&4&l赌徒! &c你赌输了。"))
            return
        }

        val target = entity as? Player ?: return
        PlayerUtil.damage(target, PlayerUtil.DamageType.TRUE, getDamage(enchantLevel), false)
        target.sendMessage(CC.translate("&4&l赌徒! &c你受到来自对方的 &f" + (getDamage(enchantLevel) / 2) + "❤ &c必中伤害."))
        attacker.sendMessage(CC.translate("&2&l赌徒! &a对目标 &f${target.name} &a造成了伤害!"))

//        var hit = 0
//
//        if (attacker.itemInHand != null) hit = PlayerProfile.getRawCache(attacker.uniqueId).meleeHit
//
//        val victim = entity as? Player ?: return
//        if (hit % 2 != 0) return
//        if (attacker.health >= (2 * enchantLevel + 2)) {s
//            PlayerUtil.damage(attacker, PlayerUtil.DamageType.TRUE, enchantLevel.toDouble() * 2 - 1.0, false)
//        }
//        PlayerUtil.damage(victim, PlayerUtil.DamageType.TRUE, enchantLevel.toDouble() * 2 - 1.0, false)
//    }

//    override fun getText(p0: Int, player: Player?): String {
//        return getHitActionText(player, 2)
//    }
    }
}