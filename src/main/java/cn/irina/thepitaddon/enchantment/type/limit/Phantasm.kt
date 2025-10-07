package cn.irina.thepitaddon.enchantment.type.limit

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.data.PhantasmState
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.IActionDisplayEnchant
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Bukkit
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/*
 * @Author Irina
 * @Date 2025/10/3 23:59
 */

@ArmorOnly
class Phantasm : AbstractEnchantment(), Listener, IActionDisplayEnchant {

    override fun getEnchantName(): String = "虚影"
    override fun getRarity(): EnchantmentRarity = EnchantmentRarity.OP
    override fun getCooldown(): Cooldown? = null
    override fun getMaxEnchantLevel(): Int = 3
    override fun getNbtName(): String = "phantasm"

    override fun getUsefulnessLore(i: Int): String {
        val duration = i * 0.75
        val speedBonus = (i * SPEED_MULTIPLIER_PER_LEVEL * 100).toInt()
        return "&7穿戴附有此附魔的 &e神话之甲 &7时 /s" +
                "&7单击下蹲键将触发效果 &8虚化 (${duration}s) &7(9s冷却) /s" +
                "&7效果 &8虚化&7: &7在 &e${duration} &7秒内无法被攻击, 在此期间移速增加 &b${speedBonus}% /s" +
                "&7但同时, 自身也无法攻击目标"
    }

    private val instance = Main.instance
    private val pitApi = ThePit.getApi()
    private val stateMap = ConcurrentHashMap<UUID, PhantasmState>()
    private val lastAccessTimeMap = ConcurrentHashMap<UUID, Long>()

    companion object {
        const val COOLDOWN_SECONDS = 9L
        const val PHANTASM_KEEP_TIME = 15L
        const val PERFECT_DODGE_WINDOW_MS = 600L
        const val PERFECT_PHANTASM_KEEP_TIME = 18L
        const val PERFECT_PHANTASM_HEAL = 8.0
        const val SPEED_MULTIPLIER_PER_LEVEL = 0.15F
        const val CLEANUP_INTERVAL_TICKS = 20L * 60L * 20L
        const val STATE_EXPIRE_TIME_MS = 30L * 60L * 1000L
    }

    init {
        startCleanupTask()
    }

    private fun getState(player: Player): PhantasmState {
        lastAccessTimeMap[player.uniqueId] = System.currentTimeMillis()
        return stateMap.getOrPut(player.uniqueId) { PhantasmState() }
    }

    private fun scheduleDeactivation(player: Player, state: PhantasmState, keepTime: Long) {
        state.task?.cancel()
        state.task = Bukkit.getScheduler().runTaskLater(instance, {
            state.isActive = false
            state.isPerfect = false

            if (player.isOnline) {
                player.walkSpeed = state.defaultSpeed
            }

            state.task = null
        }, keepTime)
    }

    @EventHandler
    fun onSneak(evt: PlayerToggleSneakEvent) {
        val player = evt.player
        if (!player.isSneaking) return

        val leggings = player.inventory.leggings ?: return
        val level = pitApi.getItemEnchantLevel(leggings, nbtName)
        if (level < 1) return

        val state = getState(player)
        if (!state.cooldown.hasExpired()) return

        state.cooldown = Cooldown(COOLDOWN_SECONDS, TimeUnit.SECONDS)
        state.level = level
        state.startTime = System.currentTimeMillis()
        state.isActive = true

        player.sendMessage(CC.translate("&8かむい!"))
        player.walkSpeed = state.defaultSpeed * (1 + (level * SPEED_MULTIPLIER_PER_LEVEL))
        scheduleDeactivation(player, state, calculateDuration(level))
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onDefense(evt: EntityDamageByEntityEvent) {
        val victim = evt.entity as? Player ?: return

        val state = getState(victim)
        if (!state.isActive) return

        evt.isCancelled = true

        val currentTime = System.currentTimeMillis()
        val timeSinceActivation = currentTime - state.startTime

        if (timeSinceActivation <= PERFECT_DODGE_WINDOW_MS && !state.isPerfect) {
            victim.sendMessage(CC.translate("&8虚影 &7完美虚化!"))
            state.isPerfect = true
            PlayerUtil.heal(victim, PERFECT_PHANTASM_HEAL)
            scheduleDeactivation(victim, state, calculatePerfectDuration(state.level))
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onAttack(evt: EntityDamageByEntityEvent) {
        val damager = if (evt.damager is Arrow) {
            (evt.damager as Arrow).shooter as? Player ?: return
        } else {
            evt.damager as? Player ?: return
        }

        val state = getState(damager)
        if (!state.isActive) return

        evt.isCancelled = true
        damager.sendMessage(CC.translate("&8虚影 &7虚化状态下无法攻击!"))
    }

    @EventHandler
    fun onQuit(evt: PlayerQuitEvent) {
        cleanupPlayer(evt.player)
    }

    @EventHandler
    fun onKick(evt: PlayerKickEvent) {
        cleanupPlayer(evt.player)
    }

    @EventHandler
    fun onDeath(evt: org.bukkit.event.entity.PlayerDeathEvent) {
        cleanupPlayer(evt.entity)
    }

    private fun cleanupPlayer(player: Player) {
        val playerState = stateMap.remove(player.uniqueId) ?: return
        lastAccessTimeMap.remove(player.uniqueId)
        playerState.cleanUp(player)
    }

    private fun calculateDuration(level: Int): Long {
        return level * PHANTASM_KEEP_TIME
    }

    private fun calculatePerfectDuration(level: Int): Long {
        return level * PERFECT_PHANTASM_KEEP_TIME
    }

    private fun startCleanupTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, {
            val currentTime = System.currentTimeMillis()
            val expiredPlayers = mutableListOf<UUID>()

            lastAccessTimeMap.forEach { (uuid, lastTime) ->
                if (currentTime - lastTime > STATE_EXPIRE_TIME_MS) {
                    expiredPlayers.add(uuid)
                }
            }

            expiredPlayers.forEach { uuid ->
                stateMap.remove(uuid)?.cleanUp()
                lastAccessTimeMap.remove(uuid)
            }

            if (expiredPlayers.isNotEmpty()) {
                Bukkit.getLogger().info("[Phantasm] 清理了 ${expiredPlayers.size} 个过期的玩家状态")
            }
        }, CLEANUP_INTERVAL_TICKS, CLEANUP_INTERVAL_TICKS)
    }

    override fun getText(p0: Int, p: Player): String {
        return getCooldownActionText(getState(p).cooldown)
    }
}