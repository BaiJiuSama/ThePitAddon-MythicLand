package cn.irina.thepitaddon.enchantment.type.limit

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.libs.core.collection.ConcurrentHashSet
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/*
 * @Author Irina
 * @Date 2025/10/3 23:59
 */

@ArmorOnly
class Phantasm: AbstractEnchantment(), Listener, CoroutineScope {
    private val job = Job()
    override val coroutineContext = job + Dispatchers.IO

    override fun getEnchantName(): String {
        return "虚影"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.OP
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "phantasm"
    }

    override fun getUsefulnessLore(i: Int): String {
        val duration = i * 0.75
        return "&7穿戴附有此附魔的 &e神话之甲 &7时 /s" +
                "&7单击下蹲键将触发效果 &9虚化 (${duration}s) /s" +
                "&7效果 &9虚化&7: &f在 &e${duration}秒内无法被攻击, 在此期间移速增加 &b${i * 10}% /s"  +
                "&7但同时, 自身也无法攻击目标"
    }

    private val cancelTask = ConcurrentHashMap<UUID, BukkitTask>()
    private val enchantLevel = ConcurrentHashMap<UUID, Int>()
    private val instance = Main.instance

    private val actives = ConcurrentHashSet<UUID>()
    private val cooldown = ConcurrentHashMap<UUID, Cooldown>()
    private val elapsed = ConcurrentHashMap<UUID, Long>()
    private val pitApi = ThePit.getApi()
    private val defaultSpeed = ConcurrentHashMap<UUID, Float>()

    @EventHandler
    fun onSneak(evt: PlayerToggleSneakEvent) {
        launch {
            val player = evt.player
            val level = pitApi.getItemEnchantLevel(player.inventory.leggings, nbtName)
            if (level < 1) return@launch

            val cd = cooldown[player.uniqueId] ?: return@launch
            if (!cd.hasExpired()) return@launch

            enchantLevel[player.uniqueId] = level

            elapsed[player.uniqueId] = System.currentTimeMillis()
            actives.add(player.uniqueId)

            val speed = player.walkSpeed
            defaultSpeed[player.uniqueId] = speed

            launch(Dispatchers.Main) {
                player.walkSpeed *= level * 0.1F
                cancelTask[player.uniqueId] = Bukkit.getScheduler().runTaskLaterAsynchronously(instance, {
                    actives.remove(player.uniqueId)
                    player.walkSpeed = defaultSpeed[player.uniqueId] ?: 0.2F
                }, level * 15L)
            }
        }
    }

    @EventHandler
    fun onDefense(evt: EntityDamageByEntityEvent) {
        val damager = evt.damager as? Player ?: return
        launch {
            val victim = evt.entity as? Player ?: return@launch
            if (!actives.contains(victim.uniqueId)) return@launch
            evt.isCancelled = true

            val elapsedTime = elapsed[victim.uniqueId] ?: return@launch
            val currentTime = System.currentTimeMillis()

            if (currentTime - elapsedTime > 100L) return@launch
            victim.sendMessage(CC.translate("&8虚影 &7完美虚化!"))
            val level = enchantLevel[victim.uniqueId] ?: return@launch

            cancelTask[victim.uniqueId]?.cancel() ?: return@launch
            cancelTask[victim.uniqueId] = Bukkit.getScheduler().runTaskLaterAsynchronously(instance, {
                actives.remove(victim.uniqueId)
            }, level * 18L)

            launch(Dispatchers.Main) {
                PlayerUtil.heal(victim, 6.0)
            }
        }
    }

    @EventHandler
    fun onAttack(evt: EntityDamageByEntityEvent) {
        val victim = evt.entity as? Player ?: return
        launch {
            val damager = evt.damager as? Player ?: return@launch
            if (!actives.contains(damager.uniqueId)) return@launch
            launch(Dispatchers.Main) {
                evt.isCancelled = true
                damager.sendMessage(CC.translate("&8虚影 &7虚化状态下无法攻击!"))
            }
        }
    }
}