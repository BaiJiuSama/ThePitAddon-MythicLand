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
class Gamble: AbstractEnchantment(), IAttackEntity {
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
        return "&7攻击时, 将对目标和自身随机选择一位目标 /s" +
                "&7额外造成 &f${enchantLevel.toDouble()}❤ &7的&f必中&7伤害 /s" +
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
        if (RandomUtil().hasSuccessfullyByChance(0.5)) {
            PlayerUtil.damage(attacker, PlayerUtil.DamageType.TRUE, enchantLevel * 2.0, false)
            attacker.sendMessage(CC.translate("&4赌徒! &7你赌输了。"))
            return
        }

        val target = entity as? Player ?: return
        PlayerUtil.damage(target, PlayerUtil.DamageType.TRUE, enchantLevel * 2.0, false)
        attacker.sendMessage(CC.translate("&4赌徒! &7对目标 &f${target.name} &7造成了伤害!"))
    }
}