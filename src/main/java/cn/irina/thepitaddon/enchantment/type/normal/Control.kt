package cn.irina.thepitaddon.enchantment.type.normal

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.param.item.ArmorOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.parm.listener.IPlayerDamaged
import cn.charlotte.pit.util.cooldown.Cooldown
import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

import java.util.concurrent.atomic.AtomicBoolean

@ArmorOnly
class Control : AbstractEnchantment(),  IPlayerDamaged {
    private val pitAPI = ThePit.getApi()

    override fun getEnchantName(): String? {
        return "掌控"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String? {
        return "control"
    }

    override fun getRarity(): EnchantmentRarity? {
        return EnchantmentRarity.NORMAL
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String? {
        return "&7自身受到的伤害 &9-${2 + (enchantLevel * 2)}% /s" +
                "&7同时, 若攻击者手持武器含有附魔 &d扼杀 &7时 /s" +
                "&7自身将获得 &6${0.4 + (0.4 * enchantLevel)}❤ 生命吸收 &7效果"
    }

    override fun handlePlayerDamaged(
        enchantLevel: Int,
        player: Player,
        entity: Entity,
        p3: Double,
        p4: AtomicDouble?,
        boostDamage: AtomicDouble,
        p6: AtomicBoolean?
    ) {
        boostDamage.set(boostDamage.get() * (1 + (0.02 + (enchantLevel * 0.02))))

        val attacker = entity as? Player ?: return
        if (pitAPI.getItemEnchantLevel(attacker.itemInHand, "stifle") <= 0) return

        val craftPlayer = player as CraftPlayer
        craftPlayer.handle.absorptionHearts += 2 * (0.4 + (0.4 * enchantLevel)).toFloat()
    }
}