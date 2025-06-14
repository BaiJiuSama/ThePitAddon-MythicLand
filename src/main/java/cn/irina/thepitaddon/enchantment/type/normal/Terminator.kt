package cn.irina.thepitaddon.enchantment.type.normal

import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.WeaponOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IAttackEntity
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@WeaponOnly
class Terminator : AbstractEnchantment(), IAttackEntity {
    override fun getEnchantName(): String {
        return "终结者"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "terminator"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.NORMAL
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7对穿着 &7" + "&f锁链装备" + "&7的玩家造成的伤害 &c+" + (25 + 50 * enchantLevel) + "%"
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
        if (entity !is Player) return
        onDamage(entity, enchantLevel, boostDamage)
    }

    private fun onDamage(player: Player, enchantLevel: Int, boostDamage: AtomicDouble) {
        if (!isChainmailArmor(player)) return
        boostDamage.getAndAdd((25 + (50 * enchantLevel)) * 0.01)
    }

    companion object {
        private val chainmailArmorTypes: Set<Material> = EnumSet.of(
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS
        )
    }

    private fun isChainmailArmor(player: Player): Boolean {
        val armor = player.inventory.armorContents
        for (item in armor) {
            if (chainmailArmorTypes.contains(item.type)) {
                return true
            }
        }
        return false
    }
}