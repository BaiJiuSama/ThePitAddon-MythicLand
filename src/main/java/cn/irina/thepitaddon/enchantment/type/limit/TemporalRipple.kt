package cn.irina.thepitaddon.enchantment.type.limit

import cn.irina.thepitaddon.Main
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerDamaged
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/*
 * @Author Irina
 * @Date 2025/8/29 12:04
 */

@ArmorOnly
class TemporalRipple: AbstractEnchantment(), IPlayerDamaged{
    override fun getEnchantName(): String {
        return "时间涟漪"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "temporal_ripple"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.OP
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(i: Int): String {
        return "&7穿戴含有此附魔的神话之甲时 /s" +
                "&7若受击时处于 &f格挡 &7状态时, 将获得 &e3s &b时空扭曲 &7效果 &7(10s冷却)/s" +
                "&7效果 &b时空扭曲&f: 受击间隔时长增加 &e${i * 0.25}s"
    }

    private val instance = Main.instance
    private val cdMap = ConcurrentHashMap<UUID, Cooldown>()
    override fun handlePlayerDamaged(level: Int, myself: Player, entity: Entity, p3: Double, p4: AtomicDouble, p5: AtomicDouble, p6: AtomicBoolean, ) {
        if (entity !is Player) return
        if (!myself.isBlocking) return
//
//        val cd = cdMap[myself.uniqueId] ?: Cooldown(0L)
//        if (!cd.hasExpired()) return
//        cdMap[myself.uniqueId] = Cooldown(10L, TimeUnit.SECONDS)
//
//        val boostTick = level * 5
//
//        myself.noDamageTicks += boostTick
//        myself.sendMessage(CC.translate("&b&l时间涟漪! &7效果触发!"))
//
//        Bukkit.getScheduler().runTaskLater(instance, {
//            val me = if (!myself.isOnline) Bukkit.getOfflinePlayer(myself.uniqueId).player else myself
//            me.noDamageTicks -= boostTick
//            me.sendMessage(CC.translate("&b&l时间涟漪! &7效果结束..."))
//        }, 3 * 20L)
    }

//    @EventHandler
//    fun onQuit(event: PlayerQuitEvent) { event.player.noDamageTicks = 20 }
//
//    @EventHandler
//    fun onDeath(event: PlayerDeathEvent) {
//        val player = event.entity
//        val uuid = player.uniqueId
//        if (cdMap[uuid] != null) cdMap.remove(uuid)
//
//        player.noDamageTicks = 20
//    }
}