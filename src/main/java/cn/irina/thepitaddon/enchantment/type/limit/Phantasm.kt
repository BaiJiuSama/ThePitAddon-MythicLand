package cn.irina.thepitaddon.enchantment.type.limit

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.data.PhantasmState
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Bukkit
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

/**
 * @Author Irina
 * @Date 2025/10/3 23:59
 */

@ArmorOnly
class Phantasm : AbstractEnchantment(), Listener {

    override fun getEnchantName(): String = "虚影"
    override fun getRarity(): EnchantmentRarity = EnchantmentRarity.OP
    override fun getCooldown(): Cooldown? = null
    override fun getMaxEnchantLevel(): Int = 3
    override fun getNbtName(): String = "phantasm"

    override fun getUsefulnessLore(i: Int): String {
        val duration = i * 0.75
        return "&7穿戴附有此附魔的 &e神话之甲 &7时 /s" +
                "&7单击下蹲键将触发效果 &9虚化 (${duration}s) &8(9s冷却) /s" +
                "&7效果 &9虚化&7: &f在 &e${duration}秒内无法被攻击, 在此期间移速增加 &b${i * 10}% /s" +
                "&7但同时, 自身也无法攻击目标"
    }

    companion object {
        const val COOLDOWN_SECONDS = 9L
        const val PHANTASM_KEEP_TIME = 15L
        const val PERFECT_DODGE_WINDOW_MS = 100L
        const val PERFECT_PHANTASM_KEEP_TIME = 18L
        const val PERFECT_PHANTASM_HEAL = 6.0
    }

    private val instance = Main.instance
    private val pitApi = ThePit.getApi()
    private val state = ConcurrentHashMap<UUID, PhantasmState>()

    private val activePlayers = Collections.newSetFromMap(ConcurrentHashMap<UUID, Boolean>())

    private fun getState(player: Player): PhantasmState {
        return state.getOrPut(player.uniqueId) { PhantasmState() }
    }

    private fun scheduleDeactivation(player: Player, state: PhantasmState, keepTime: Long) {
        state.task?.cancel()
        state.task = Bukkit.getScheduler().runTaskLater(instance, {
            state.isActive = false
            state.isPerfect = false

            activePlayers.remove(player.uniqueId)
            player.walkSpeed = state.defaultSpeed
            state.task = null
        }, keepTime)
    }

    @EventHandler
    fun onSneak(evt: PlayerToggleSneakEvent) {
        val player = evt.player
        if (!player.isSneaking) return

        val level = pitApi.getItemEnchantLevel(player.inventory.leggings, nbtName)
        if (level < 1) return

        val state = getState(player)
        if (!state.cooldown.hasExpired()) return

        state.cooldown = Cooldown(COOLDOWN_SECONDS, TimeUnit.SECONDS)
        state.level = level
        state.startTime = System.currentTimeMillis()
        state.isActive = true
        activePlayers.add(player.uniqueId)

        player.walkSpeed = state.defaultSpeed * (1 + level * 0.1F)
        scheduleDeactivation(player, state, calculateDuration(level))
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onDefense(evt: EntityDamageByEntityEvent) {
        if (evt.entity !is Player || evt.damager !is Player) return
        if (evt.isCancelled) return

        val victim = evt.entity as Player

        if (!activePlayers.contains(victim.uniqueId)) return

        val state = state[victim.uniqueId] ?: return
        if (!state.isActive) return

        evt.isCancelled = true

        val currentTime = System.currentTimeMillis()
        if (currentTime - state.startTime > PERFECT_DODGE_WINDOW_MS) return

        victim.sendMessage(CC.translate("&8虚影 &7完美虚化!"))
        state.isPerfect = true
        PlayerUtil.heal(victim, PERFECT_PHANTASM_HEAL)

        scheduleDeactivation(victim, state, calculatePerfectDuration(state.level))
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onAttack(evt: EntityDamageByEntityEvent) {
        if (evt.damager !is Player || evt.entity !is Player) return
        if (evt.isCancelled) return

        val damager = evt.damager as Player

        if (!activePlayers.contains(damager.uniqueId)) return

        val state = state[damager.uniqueId] ?: return
        if (!state.isActive) return

        evt.isCancelled = true
        damager.sendMessage(CC.translate("&8虚影 &7虚化状态下无法攻击!"))
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) { cleanupPlayer(e.player) }

    @EventHandler
    fun onKick(e: PlayerKickEvent) { cleanupPlayer(e.player) }

    private fun cleanupPlayer(player: Player) {
        val state = state.remove(player.uniqueId) ?: return
        activePlayers.remove(player.uniqueId)
        state.cleanUp(player)
    }

    private fun calculateDuration(level: Int): Long = level * PHANTASM_KEEP_TIME
    private fun calculatePerfectDuration(level: Int): Long = level * PERFECT_PHANTASM_KEEP_TIME
}