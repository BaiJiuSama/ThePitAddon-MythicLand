package cn.irina.thepitaddon.enchantment.type.recode.rare

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.utils.LocationUtil
import com.google.common.util.concurrent.AtomicDouble
import net.minecraft.server.v1_8_R3.DamageSource.arrow
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.BowOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerShootEntity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

/*
 * @Author Irina
 * @Date 2025/6/11 16:48
 */

@BowOnly
class EnderBow: AbstractEnchantment(), IPlayerShootEntity, IActionDisplayEnchant, Listener {
    override fun getEnchantName(): String {
        return "末影弓"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "ender_bow_enchant"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(level: Int): String {
        return StringBuilder("&7射击时若自身处于潜行状态, 射出的箭矢会将自身传送至箭矢落地点 (${120 - (level * 30)}s冷却) /s")
            .append("&7(传送后视角将转向以自身为中心 &e10 &7格内距离自身最近的目标)")
            .append("&7(箭矢命中目标时减少冷却3s)")
            .toString()
    }

    val cooldown = ConcurrentHashMap<UUID, Cooldown>()
    override fun handleShootEntity(
        level: Int,
        player: Player,
        entity: Entity,
        p3: Double,
        p4: AtomicDouble?,
        p5: AtomicDouble?,
        p6: AtomicBoolean?,
    ) {
        cooldown.putIfAbsent(player.uniqueId, Cooldown(0L))
        cooldown[player.uniqueId] = Cooldown(0L.coerceAtLeast(cooldown[player.uniqueId]!!.remaining - 3000L))
    }

    @EventHandler
    fun onShoot(event: EntityShootBowEvent) {
        if (ThePit.getInstance().eventFactory.activeEpicEvent != null) return

        val player = event.entity as? Player ?: return
        if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) return

        cooldown.putIfAbsent(player.uniqueId, Cooldown(0L))
        if (!cooldown[player.uniqueId]!!.hasExpired() || !player.isSneaking) return

        val itemInHand = player.itemInHand
        if (itemInHand == null || itemInHand.type == Material.AIR || itemInHand.type != Material.BOW) return

        val level = ThePit.getApi().getItemEnchantLevel(itemInHand, this.nbtName)
        if (level <= 0) return

        cooldown[player.uniqueId] = Cooldown((120 - (level * 30)).toLong(), TimeUnit.SECONDS)
        event.projectile.setMetadata("ender_bow", FixedMetadataValue(Main.instance, true))

    }

    @EventHandler
    fun onBowHit(event: ProjectileHitEvent) {
        if (!event.getEntity().hasMetadata("ender_bow") || event.getEntity().shooter == null) return
        val player = event.getEntity().shooter as? Player ?: return
        val arrow = event.entity as? Arrow ?: return

        if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) return
        player.teleport(event.getEntity().location)

        val nearestPlayer: Player = LocationUtil.getNearbyPlayer(arrow, 10) ?: return

        val myLocation = player.location.clone()
        val targetLocation = nearestPlayer.location.clone()

        val dx = targetLocation.x - player.location.x
        val dy = targetLocation.y - player.location.y
        val dz = targetLocation.z - player.location.z

        var yaw = Math.toDegrees(atan2(-dx, dz))
        val pitch = Math.toDegrees(atan(-dy / sqrt(dx * dx + dz * dz)))

        yaw = (yaw + 360) % 360

        myLocation.yaw = yaw.toFloat()
        myLocation.pitch = pitch.toFloat()
        player.teleport(myLocation)
    }

    override fun getText(p0: Int, player: Player): String {
        if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) return "&c&l✘"
        return getCooldownActionText(cooldown.getOrDefault(player.uniqueId, Cooldown(0L)))
    }
}