package cn.irina.thepitaddon.enchantment.type.limit

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.manager.PitManager
import cn.irina.thepitaddon.utils.TimeUtil
import com.google.common.util.concurrent.AtomicDouble
import net.minecraft.server.v1_8_R3.ItemBow
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.BowOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerShootEntity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.Utils
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerQuitEvent
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
class DesertRose : AbstractEnchantment(), IPlayerShootEntity, Listener, IActionDisplayEnchant {

    companion object {
        private val cooldown: HashMap<UUID, Cooldown> = HashMap()
        private val laserCooldown: HashMap<UUID, Cooldown> = HashMap()
    }

    private val pitApi = ThePit.getApi()

    override fun getEnchantName(): String {
        return "沙漠玫瑰"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "desert_rose"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.OP
    }

    override fun getCooldown(): Cooldown? {
        return null
    }


    override fun getUsefulnessLore(enchantLevel: Int): String {
        val buffLevel = when (enchantLevel) {
            1 -> "I"
            2 -> "I"
            3 -> "II"
            else -> RomanUtil.convert(enchantLevel - 1)
        }
        val speedBuffLevel = RomanUtil.convert(enchantLevel + 1)
        val speedDuration = TimeUtil.formatTotalSeconds(enchantLevel * 2 + 4)
        val slowDuration = TimeUtil.formatTotalSeconds(enchantLevel)
        val witherDuration = TimeUtil.formatTotalSeconds(enchantLevel)
        return "&7射箭时无需蓄力即可让箭矢以最大蓄力状态射出, 若命中目标则召唤一道激光./s" +
                "&7激光命中敌人时, 将会产生一次爆炸并对周围敌人施加 &c缓慢 ${buffLevel} &f($slowDuration) &7与 &8凋零 ${buffLevel} &f($witherDuration)/s" +
                "&7同时, 若爆炸接触目标生命值低于 &c${enchantLevel * 0.5 + 1.0}❤ &7时, 将直接致死. /s" +
                "&7若射箭时处于潜行状态且激光命中目标时, 则将自身传送至爆炸处, 并获得 &b速度 ${speedBuffLevel} &f($speedDuration) &7(15秒冷却) /s"+
                "&7此附魔每秒只能触发一次."
    }

    @EventHandler
    fun onInteract(event: EntityShootBowEvent) {
        if (event.entity !is Player) return
        if (event.force >= 1) return
        val player = event.entity as Player
        if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) return
        val itemInHand = player.itemInHand ?: return
        val level = pitApi.getItemEnchantLevel(player.itemInHand, this.nbtName)
        if (level == -1) {
            return
        }
        if (itemInHand.type != Material.BOW) return
        if (!Companion.cooldown.getOrDefault(player.uniqueId, Cooldown(0)).hasExpired()) return
        val itemStack = Utils.toNMStackQuick(itemInHand)
        val bow = itemStack.item as ItemBow
        Companion.cooldown[player.uniqueId] = Cooldown(1, TimeUnit.SECONDS)
        val ePlayer = (player as CraftPlayer).handle
        if ((event.entity as Player).isSneaking) {
            if (!Companion.laserCooldown.getOrDefault(player.uniqueId, Cooldown(0)).hasExpired()) return
            shootLaser(player, level, true)
            event.isCancelled = true
            return
        }
        bow.a(itemStack, ePlayer.world, ePlayer, 0)
        event.isCancelled = true
    }

    private fun shootLaser(player: Player, level: Int, isSneaking: Boolean = false) {
        val startLocation = player.eyeLocation.clone()
        val direction = startLocation.direction.normalize()
        val laserLength = (level * 10 + 10) * 2
        if (isSneaking) {
            for (i in 0..laserLength * 2) {
                val particleLocation = startLocation.clone().add(direction.clone().multiply(i * 0.5))
                Bukkit.getOnlinePlayers()
                    .forEach { targetPlayer -> sendRedstoneParticle(targetPlayer, particleLocation, 102f, 204f, 255f) }
                val nearbyEntities = particleLocation.world.getNearbyEntities(particleLocation, 0.5, 0.5, 0.5)
                for (entity in nearbyEntities) {
                    if (entity !is Player || entity == player || entity.isDead || entity.location.y > 100) continue
                    val directlyHitPlayer = entity as Player
                    val targetLocation = directlyHitPlayer.location.clone().add(0.0, 0.5, 0.0)
                    applyLaserEffects(player, directlyHitPlayer, targetLocation, level)
                    vampireWithPerk(player)
                    execution(player, directlyHitPlayer, level)
                    laserCooldown[player.uniqueId] = Cooldown(15, TimeUnit.SECONDS)
                    PitManager.givePlayerSpeedBuff(player, level * 2 * 20 + 40, level)
                    return

                }
            }
        } else {
            for (i in 0..laserLength) {
                val particleLocation = startLocation.clone().add(direction.clone().multiply(i * 0.5))
                Bukkit.getOnlinePlayers()
                    .forEach { targetPlayer -> sendRedstoneParticle(targetPlayer, particleLocation, 255f, 0f, 0f) }
                val nearbyEntities = particleLocation.world.getNearbyEntities(particleLocation, 0.5, 0.5, 0.5)
                for (entity in nearbyEntities) {
                    if (entity !is Player || entity == player || entity.isDead || entity.location.y > 100) continue
                    val directlyHitPlayer = entity
                    val targetLocation = directlyHitPlayer.location.clone().add(0.0, 0.5, 0.0)
                    applyLaserEffects(player, directlyHitPlayer, targetLocation, level)
                    vampireWithPerk(player)
                    execution(player, directlyHitPlayer, level)
                    laserCooldown[player.uniqueId] = Cooldown(1, TimeUnit.SECONDS)
                    return
                }
            }
        }
        laserCooldown[player.uniqueId] = Cooldown(1, TimeUnit.SECONDS)
    }

    private fun vampireWithPerk(player: Player) {
        if (PlayerUtil.isPlayerChosePerk(player, "Vampire")) {
            PlayerUtil.heal(player, 3.0)
        }
    }

    private fun execution(attacker: Player, target: Player, level: Int) {
        val healthThreshold = level * 0.5 + 1.0
        if (target.health <= healthThreshold * 2) {
            PlayerUtil.damage(
                attacker,
                target,
                PlayerUtil.DamageType.TRUE,
                healthThreshold * 2,
                false
            )
        }
    }

    private fun applyLaserEffects(attacker: Player, target: Player, location: Location, level: Int) {
        val maxDamage = 4.0
        target.damage(maxDamage)
        location.world.playSound(location, Sound.EXPLODE, 1.0f, 1.0f)
        location.world.playEffect(location, Effect.EXPLOSION_HUGE, null)
        if (attacker.isSneaking) {
            val attackerTeleportLocation = location.clone().add(0.0, 0.5, 0.0)
            attacker.teleport(attackerTeleportLocation)
        }
        val nearbyPlayers = location.world.getNearbyEntities(location, 4.0, 4.0, 4.0)
        for (entity in nearbyPlayers) {
            if (entity !is Player || entity == target || entity == attacker || entity.isDead) continue
            val distance = entity.location.distance(location)
            if (distance <= maxDamage) {
                val damage = ((maxDamage - distance) * 2).toFloat()
                entity.damage(damage.toDouble())
                setVector(entity, location)
                applyEffect(entity, level)
            }
        }
    }

    private fun applyEffect(target: Player, level: Int) {
        if (target.hasPotionEffect(PotionEffectType.SLOW)) target.removePotionEffect(PotionEffectType.SLOW)
        if (target.hasPotionEffect(PotionEffectType.WITHER)) target.removePotionEffect(PotionEffectType.WITHER)

        val buffLevel = when (level) {
            1 -> 0 // 缓慢 I
            2 -> 0 // 缓慢 I
            3 -> 1 // 缓慢 II
            else -> level - 2
        }
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * level, buffLevel))
        target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 20 * level, buffLevel))
    }

    private fun setVector(
        entity: Entity,
        location: Location,
        x: Double = 0.6,
        y: Double = 0.4
    ) {
        val vector = entity.location.toVector().subtract(location.toVector()).normalize()
        vector.multiply(x)
        vector.setY(y)
        entity.velocity = vector
    }

    private fun sendRedstoneParticle(sender: Player, location: org.bukkit.Location, r: Float, g: Float, b: Float) {
        val packet = net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles(
            net.minecraft.server.v1_8_R3.EnumParticle.REDSTONE,
            true,
            location.x.toFloat(),
            location.y.toFloat(),
            location.z.toFloat(),
            r / 255,
            g / 255,
            b / 255,
            1.0f,
            0
        )
        (sender as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        Companion.cooldown.remove(e.player.uniqueId)
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
        if (!Companion.laserCooldown.getOrDefault(attacker.uniqueId, Cooldown(0)).hasExpired()) return
        if (target !is Player) return
        shootLaser(attacker, enchantLevel)
    }


    override fun getText(level: Int, player: Player): String {
        return getCooldownActionText(Companion.laserCooldown.getOrDefault(player.uniqueId, Cooldown(0L)))
    }
}