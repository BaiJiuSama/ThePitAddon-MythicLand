package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.IActionDisplayEnchant
import cn.charlotte.pit.enchantment.param.item.ArmorOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.parm.listener.IAttackEntity
import cn.charlotte.pit.parm.listener.IPlayerDamaged
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.cooldown.Cooldown
import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@ArmorOnly
class ComboLadder : AbstractEnchantment(),  IAttackEntity, IPlayerDamaged, IActionDisplayEnchant {
    private val comboLayers = HashMap<UUID, Int>()

    override fun getEnchantName(): String {
        return "连击高手"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "combo_ladder"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7每当你击中一个目标, 你的攻击力将会 &c+" + enchantLevel * 2.5 + "% &7(上限&e10&7层) /s" +
                "&7但同时, 若你被任意目标所攻击时, 你的层数将会清零"
    }

    override fun handleAttackEntity(
        enchantLevel: Int,
        attacker: Player,
        entity: Entity,
        v: Double,
        atomicDouble: AtomicDouble,
        boostDamage: AtomicDouble,
        atomicBoolean: AtomicBoolean
    ) {
        if (entity !is Player) return
        val attackerUUID = attacker.uniqueId

        val layers = comboLayers.getOrDefault(attackerUUID, 0)
        if (layers < 10) comboLayers[attackerUUID] = comboLayers.getOrDefault(attackerUUID, 0) + 1

        if (layers <= 0) return

        boostDamage.getAndAdd(layers * (enchantLevel * 0.025))
    }

    override fun handlePlayerDamaged(
        enchantLevel: Int,
        victim: Player,
        entity: Entity,
        v: Double,
        atomicDouble: AtomicDouble,
        atomicDouble1: AtomicDouble,
        atomicBoolean: AtomicBoolean
    ) {
        if (entity !is Player) return

        comboLayers.replace(victim.uniqueId, 0)
    }

    override fun getText(i: Int, player: Player): String {
        return CC.translate("&e层数: " + comboLayers.getOrDefault(player.uniqueId, 0))
    }
}
