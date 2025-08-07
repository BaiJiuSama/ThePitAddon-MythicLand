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
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
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
    private val speedBoostCooldown: HashMap<UUID, Cooldown> = HashMap()

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
        return "&7射箭时无需蓄力即可让箭矢以最大蓄力状态射出,/s" +
                "&7同时, 为自身提升一级速度并延长效果 &e${enchantLevel + 1} &7秒 (2秒冷却) /s" +
                "&7(上限为${enchantLevel * 4} &7秒, 等级最高为${RomanUtil.convert(enchantLevel + 1)}级) /s" +
                "&7此附魔每秒只能触发一次, 触发效果提升时必须拥有速度效果/s" +
                "/s" +
                "  \"&7&o我就是闪电侠\""
    }

    override fun handleShootEntity(
        enchantLevel: Int,
        shooter: Player,
        target: Entity?,
        v: Double,
        atomicDouble: AtomicDouble?,
        atomicDouble1: AtomicDouble?,
        atomicBoolean: AtomicBoolean?
    ) {
        val existingSpeed = shooter.activePotionEffects.find { it.type == PotionEffectType.SPEED }
        if (existingSpeed == null) {
            return
        }
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
//        if (shooter.isSneaking) {
//            return
//        }
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

        if (speedBoostCooldown.getOrDefault(shooter.uniqueId, Cooldown(0L)).hasExpired()) {
            speedBoostCooldown[shooter.uniqueId] = Cooldown(2, TimeUnit.SECONDS)
            addAndBoostEffect(itemInHand, shooter)
        }
    }

    private fun addAndBoostEffect(itemInHand: ItemStack, shooter: Player) {
        val enchantLevel = this.getItemEnchantLevel(itemInHand)
        val existingSpeed = shooter.activePotionEffects.find { it.type == PotionEffectType.SPEED }
        if (existingSpeed == null) return
        var potionEffectTime = existingSpeed.duration
        var potionEffectLevel = existingSpeed.amplifier

        potionEffectTime += enchantLevel * 20 + 20
        if (potionEffectTime > enchantLevel * 20 * 4) {
            potionEffectTime = enchantLevel * 20 * 4
        }
        potionEffectLevel += 1
        if (potionEffectLevel > enchantLevel) {
            potionEffectLevel = enchantLevel
        }
        shooter.removePotionEffect(PotionEffectType.SPEED)
        shooter.addPotionEffect(
            PotionEffect(
                PotionEffectType.SPEED,
                potionEffectTime,
                potionEffectLevel,
                false,
                true
            )
        )
    }

    override fun getText(level: Int, player: Player): String {
        if (!PlayerUtil.isVenom(player)) return getCooldownActionText(
            speedBoostCooldown.getOrDefault(
                player.uniqueId,
                Cooldown(0L)
            )
        )
        return "&c&l✘"
    }
}