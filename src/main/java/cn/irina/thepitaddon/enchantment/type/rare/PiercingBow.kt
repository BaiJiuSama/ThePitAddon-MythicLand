package cn.irina.thepitaddon.enchantment.type.rare

import com.google.common.util.concurrent.AtomicDouble
import net.minecraft.server.v1_8_R3.ItemBow
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.BowOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerShootEntity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.cooldown.Cooldown
import net.mizukilab.pit.util.time.TimeUtil
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @Author: ShanguanLinG
 * @Date: 2025/7/29 00:53
 */

@BowOnly
class PiercingBow : AbstractEnchantment(), IPlayerShootEntity, Listener, IActionDisplayEnchant {
    private val maxChargeCooldown: HashMap<UUID, Cooldown> = HashMap()
    private val hitCount: HashMap<UUID, Int> = HashMap()

    override fun getEnchantName(): String {
        return "穿云弓"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "piercing_bow"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.OP
    }

    override fun getCooldown(): Cooldown? {
        return null
    }


    override fun getUsefulnessLore(enchantLevel: Int): String {
        var stringBuilder = StringBuilder("&7射箭时无需蓄力即可让箭矢以最大蓄力状态射出,/s")
        if (enchantLevel > 1)
            stringBuilder.append(
                "&7同时为自身添加 &b速度 ${RomanUtil.convert(if (enchantLevel > 1) enchantLevel - 1 else 1)} &f(${
                    TimeUtil.millisToTimer(
                        (enchantLevel + 1) * 1000L + 2000L
                    )
                })/s"
            )
        stringBuilder.append(
            "&7每 &e2 &7次箭矢射出并命中为自身添加 &b速度 ${RomanUtil.convert(enchantLevel + 1)} &f(${
                TimeUtil.millisToTimer(
                    (enchantLevel + 1) * 1000L + 2000L
                )
            })/s"
        ).append("/s\"&7&o我就是闪电侠\"")
        return stringBuilder.toString()
    }

    @EventHandler
    fun onInteract(event: EntityShootBowEvent) {
        if (event.entity !is Player) return
        if (event.force >= 1) return
        val shooter = event.entity as Player
        if (PlayerUtil.isVenom(shooter) || PlayerUtil.isEquippingSomber(shooter)) return
        val itemInHand = shooter.itemInHand ?: return
        val level = this.getItemEnchantLevel(itemInHand)
        if (level == -1) {
            return
        }
        if (itemInHand.type != Material.BOW) {
            return
        }
        if (maxChargeCooldown.getOrDefault(shooter.uniqueId, Cooldown(0L)).hasExpired()) {
            maxChargeCooldown[shooter.uniqueId] = Cooldown(1, TimeUnit.SECONDS)
            event.isCancelled = true
            val ePlayer = (shooter as CraftPlayer).handle
            val itemStack = CraftItemStack.asNMSCopy(itemInHand)
            val bow = itemStack.item as ItemBow
            bow.a(itemStack, ePlayer.world, ePlayer, 0)
        }
        val existingSpeed = shooter.activePotionEffects.find { it.type == PotionEffectType.SPEED }
        val enchantLevel = this.getItemEnchantLevel(itemInHand)
        if (existingSpeed == null) {
            shooter.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SPEED,
                    20 * (enchantLevel + 1) + 20 * 2,
                    enchantLevel - 2,
                    true
                )
            )
        } else if (existingSpeed.amplifier < enchantLevel - 2) {
            shooter.removePotionEffect(PotionEffectType.SPEED)
            shooter.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SPEED,
                    20 * (enchantLevel + 1) + 20 * 2,
                    enchantLevel - 2,
                    true
                )
            )
        }
        return
    }

    override fun handleShootEntity(
        enchantLevel: Int,
        attacker: Player,
        target: Entity,
        damage: Double,
        finalDamage: AtomicDouble?,
        boostDamage: AtomicDouble?,
        cancel: AtomicBoolean?
    ) {
        val playerId = attacker.uniqueId
        val currentHitCount = hitCount.getOrDefault(playerId, 0) + 1
        hitCount[playerId] = currentHitCount
        if (currentHitCount == 2) {
            attacker.removePotionEffect(PotionEffectType.SPEED)
            attacker.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SPEED,
                    20 * (enchantLevel + 1) + 20 * 2,
                    enchantLevel,
                    true
                )
            )
            hitCount[playerId] = 0
        } else if (currentHitCount > 2) {
            hitCount[playerId] = 0
        }
    }


    override fun getText(level: Int, player: Player): String {
        return "&e&l" + hitCount.getOrDefault(
            player.uniqueId,
            0
        ) + "/" + 2
    }
}