package cn.irina.thepitaddon.enchantment.type.recode.rare

import cn.charlotte.pit.data.PlayerProfile
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.WeaponOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IAttackEntity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicBoolean

@WeaponOnly
class Gamble : AbstractEnchantment(), IAttackEntity, IActionDisplayEnchant {
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
        return "&7每 &e2 &7次击中目标时 /s" +
                "&7对自身造成 &f${enchantLevel.toDouble() - 0.5}❤ 必中伤害 /s" +
                "&7对目标造成 &f${enchantLevel.toDouble() + 0.5}❤ 必中伤害 /s" +
                "&c(必中伤害无法被免疫与抵抗)"
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
//        if (RandomUtil().hasSuccessfullyByChance(0.5)) {
//            PlayerUtil.damage(attacker, PlayerUtil.DamageType.TRUE, enchantLevel * 2.0, false)
//            attacker.sendMessage(CC.translate("&4赌徒! &7你赌输了。"))
//            return
//        }
//
//        val target = entity as? Player ?: return
//        PlayerUtil.damage(target, PlayerUtil.DamageType.TRUE, enchantLevel * 2.0, false)
//        attacker.sendMessage(CC.translate("&4赌徒! &7对目标 &f${target.name} &7造成了伤害!"))

        var hit = 0

        if (attacker.itemInHand != null) hit = PlayerProfile.getRawCache(attacker.uniqueId).meleeHit

        val victim = entity as? Player ?: return
        if (hit % 2 != 0) return

        PlayerUtil.damage(attacker, PlayerUtil.DamageType.TRUE, enchantLevel.toDouble() - 0.5, false)
        PlayerUtil.damage(victim, PlayerUtil.DamageType.TRUE, enchantLevel.toDouble() - 0.5, false)
    }

    override fun getText(p0: Int, player: Player?): String {
        return getHitActionText(player, 2)
    }
}