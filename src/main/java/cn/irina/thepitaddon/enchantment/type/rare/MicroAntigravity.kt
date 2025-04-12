package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.param.item.ArmorOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.parm.listener.IAttackEntity
import cn.charlotte.pit.parm.listener.IPlayerDamaged
import cn.charlotte.pit.util.PlayerUtil
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.chat.RomanUtil
import cn.charlotte.pit.util.cooldown.Cooldown
import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@ArmorOnly
class MicroAntigravity : AbstractEnchantment(), IPlayerDamaged,  IAttackEntity {
    private val keepBoost = HashMap<UUID, Cooldown>()
    private val hitCounts = HashMap<UUID, Int>()
    private val lastHitTimes = HashMap<UUID, Long>()

    override fun getEnchantName(): String {
        return "微观反重力"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "micro_anti_gravity"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RAGE_RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7当自身处于空中时被连续攻击 &f3 &7次, &7(0.5s内)/s" +
                "则立刻恢复 &c2.0❤ &7生命值 并获得 &b速度 ${RomanUtil.convert(enchantLevel)} &f(00:05) /s" +
                if (enchantLevel > 1) "&7同时, 在 &e6s &7内自身伤害提升 &c+${enchantLevel * 5}%" else null
    }



    override fun handlePlayerDamaged(
        enchantLevel: Int,
        player: Player,
        entity: Entity,
        damage: Double,
        boostDamage: AtomicDouble,
        reduceDamage: AtomicDouble,
        cancel: AtomicBoolean
    ) {
        if (entity !is Player) return
        val playerUUID = player.uniqueId

        if ((player as CraftPlayer).handle.onGround) {
            hitCounts[playerUUID] = 0
            return
        }

        if (keepBoost.getOrDefault(playerUUID, Cooldown(0L)).hasExpired() && enchantLevel > 1) {
            keepBoost[playerUUID] = Cooldown(6L, TimeUnit.SECONDS)
        }

        val currentTime = System.currentTimeMillis()
        val lastHitTime = lastHitTimes.getOrDefault(playerUUID, 0L)

        if (currentTime - lastHitTime < 500) { //100 = 0.1s
            val count = hitCounts.getOrDefault(playerUUID, 0)
            hitCounts[playerUUID] = count + 1

            if (hitCounts[playerUUID] == 2) {
                val ap = PlayerProfile.getRawCache(entity.uniqueId)
                player.sendMessage(CC.translate("&4&l微观反重力! &7针对触发 " + ap.formattedNameWithRoman))

                keepBoost[playerUUID] = Cooldown(6L, TimeUnit.SECONDS)

                PlayerUtil.heal(player, 4.0)

                if (player.hasPotionEffect(PotionEffectType.SPEED)) player.removePotionEffect(PotionEffectType.SPEED)
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 5 * 20, enchantLevel - 1, false, true))

                hitCounts[playerUUID] = 0
            }
        } else {
            hitCounts[playerUUID] = 1
        }

        lastHitTimes[playerUUID] = currentTime
    }

    override fun handleAttackEntity(
        enchantLevel: Int,
        attacker: Player,
        p2: Entity,
        p3: Double,
        p4: AtomicDouble,
        boostDamage: AtomicDouble,
        p6: AtomicBoolean
    ) {
        if (keepBoost[attacker.uniqueId] == null || keepBoost[attacker.uniqueId]!!.hasExpired()) return

        boostDamage.getAndAdd(enchantLevel * 0.05)
    }
}

