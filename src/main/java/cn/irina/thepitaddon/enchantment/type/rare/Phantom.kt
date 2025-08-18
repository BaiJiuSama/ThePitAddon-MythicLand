package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.data.PlayerProfile
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IAttackEntity
import net.mizukilab.pit.parm.listener.IPlayerDamaged
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.atan2
import kotlin.math.max

@ArmorOnly
class Phantom : AbstractEnchantment(), IAttackEntity, IPlayerDamaged, IActionDisplayEnchant {

    private val cooldown = HashMap<UUID, Cooldown>()

    override fun getEnchantName(): String {
        return "幻"
    }

    override fun getMaxEnchantLevel(): Int {
        return 1
    }

    override fun getNbtName(): String {
        return "phantom"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.DARK_NORMAL
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7每 &e2 &7次击中目标时, 将瞬移到目标背后 (10s冷却)" +
                "/s&7每次成功格挡对面的攻击时, 减少4s冷却"
    }

    override fun handleAttackEntity(
        enchantLevel: Int,
        attacker: Player,
        target: Entity,
        damage: Double,
        finalDamage: AtomicDouble,
        boostDamage: AtomicDouble,
        cancel: AtomicBoolean
    ) {
        if (target !is Player) return
        if ("bot" == target.name) return
        val hit = PlayerProfile.getRawCache(attacker.uniqueId).meleeHit
        if (hit % 2 != 0) return
        if (!cooldown.getOrDefault(attacker.uniqueId, Cooldown(0L)).hasExpired()) return
        teleportBehind(attacker, target)
        cooldown[attacker.uniqueId] = Cooldown(10L, TimeUnit.SECONDS)
    }

    fun teleportBehind(player: Player, target: Player) {
        val targetLocation = target.location
        val targetDirection = targetLocation.direction.normalize()

        targetDirection.setY(0)
        targetDirection.normalize()

        val behindLocation = targetLocation.clone().subtract(targetDirection.multiply(2.0f))

        player.teleport(behindLocation)

        val playerLocation = player.location
        val deltaX = targetLocation.x - playerLocation.x
        val deltaZ = targetLocation.z - playerLocation.z

        val yaw = (atan2(deltaZ, deltaX) * (180 / Math.PI)).toFloat() - 90

        playerLocation.yaw = yaw
        playerLocation.pitch = 0f
        player.teleport(playerLocation)
    }

    override fun getText(enchantLevel: Int, player: Player): String {
        return getCooldownActionText(cooldown.getOrDefault(player.uniqueId, Cooldown(0L)))
    }

    override fun handlePlayerDamaged(
        i: Int,
        victim: Player,
        entity: Entity,
        v: Double,
        atomicDouble: AtomicDouble,
        atomicDouble1: AtomicDouble,
        cancel: AtomicBoolean
    ) {
        if (victim.isBlocking && entity is Player) {
            val currentCooldown = cooldown[victim.uniqueId]
            if (currentCooldown == null || currentCooldown.hasExpired()) return
            val newRemaining = max(0L, currentCooldown.remaining - 4000L)
            cooldown[victim.uniqueId] = Cooldown(newRemaining)
        }
    }
}