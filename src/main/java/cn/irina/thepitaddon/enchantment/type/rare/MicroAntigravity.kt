package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerDamaged
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@ArmorOnly
class MicroAntigravity : AbstractEnchantment(), IPlayerDamaged {
    private val keepBoost = HashMap<UUID, Cooldown>()
    private val hitCounts = HashMap<UUID, Int>()
    private val lastHitTimes = HashMap<UUID, Long>()

    override fun getEnchantName(): String {
        return "引力回溯"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "gravitational_backtracking"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RAGE_RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7当自身被穿着附魔 &f狂暴连击 &7时被连续攻击 &f3 &7次(0.5s内)/s" +
                "则立刻恢复 &c2.0❤ &7生命值 并获得 &b速度 ${RomanUtil.convert(enchantLevel)} &f(00:06) /s" +
                if (enchantLevel > 1) "&7同时, 在 &e6s &7内受到的伤害 &9-${6 + enchantLevel * 2}%" else null
    }

    val thePit = ThePit.getApi()
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

        if (thePit.getItemEnchantLevel(entity.inventory.leggings, "regularity") <= 0) {
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

            if (hitCounts[playerUUID] == 3) {
                val ap = PlayerProfile.getRawCache(entity.uniqueId)
                player.sendMessage(CC.translate("&b&l引力回溯! &7针对触发 " + ap.formattedNameWithRoman))

                keepBoost[playerUUID] = Cooldown(6L, TimeUnit.SECONDS)

                PlayerUtil.heal(player, 4.0)

                if (player.hasPotionEffect(PotionEffectType.SPEED)) player.removePotionEffect(PotionEffectType.SPEED)
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 5 * 20, enchantLevel - 1, false, true))

                if (keepBoost[player.uniqueId] == null || keepBoost[player.uniqueId]!!.hasExpired()) return

                reduceDamage.getAndAdd((enchantLevel * 0.02 + 0.06))

                hitCounts[playerUUID] = 0
            }
        } else {
            hitCounts[playerUUID] = 1
        }

        lastHitTimes[playerUUID] = currentTime
    }

}

