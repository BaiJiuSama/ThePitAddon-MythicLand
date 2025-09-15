package cn.irina.thepitaddon.enchantment.type.rare

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.WeaponOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

import java.util.*
import java.util.concurrent.TimeUnit

@WeaponOnly
class EnderSword : AbstractEnchantment(),  Listener, IActionDisplayEnchant {
    override fun getEnchantName(): String {
        return "末影剑"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "ender_sword"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7右键立刻扣除自身 &c" + (0.5 + (enchantLevel * 0.5)) + "❤ &7并射出一颗末影珍珠 (" + (if (enchantLevel >= 3) 20 else 30) + "s冷却) /s" +
                "&7当末影珍珠落地时, 自身获得 &b速度 " + RomanUtil.convert(enchantLevel + 1) + " &7(00:04)"
    }

    private val enchantLevel = HashMap<UUID, Int>()
    private val cooldown = HashMap<UUID, Cooldown>()

    private val pitApi = ThePit.getApi()
    private val prefix = Main.instance.PREFIX
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return
        val player = event.getPlayer()

        if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) return

        if (ThePit.getApi().getItemEnchantLevel(player.itemInHand, this.nbtName) <= 0) return

        val cd = cooldown[player.uniqueId] ?: Cooldown(0)
        if (!cd.hasExpired()) return

        val level = pitApi.getItemEnchantLevel(player.itemInHand, this.nbtName)
        val damage = ((0.5 + (level * 0.5)) * 2) + 0.5
        if (player.health <= damage) {
            player.sendMessage(CC.translate("$prefix&c你的血量不足以使用末影剑!"))
            return
        }
        cooldown[player.uniqueId] = Cooldown((if (level >= 3) 20 else 30).toLong(), TimeUnit.SECONDS)
        enchantLevel[player.uniqueId] = level

        player.launchProjectile(EnderPearl::class.java).shooter = player
        PlayerUtil.damage(player, PlayerUtil.DamageType.TRUE, damage, false)
    }

    override fun getText(i: Int, player: Player): String {
        return getCooldownActionText(cooldown.getOrDefault(player.uniqueId, Cooldown(0L)))
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val level = enchantLevel[player.uniqueId] ?: -1
        if (player == null || level <= 0 || event.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return

        if (player.hasPotionEffect(PotionEffectType.SPEED)) player.removePotionEffect(PotionEffectType.SPEED)

        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 4 * 20, level, false, true))
    }
}
