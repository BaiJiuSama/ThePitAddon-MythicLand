package cn.irina.thepitaddon.events

import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.utils.InvUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.cooldown.Cooldown
import net.mizukilab.pit.util.item.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


class PropEvent: Listener {
    private val angerList: MutableList<UUID> = ArrayList()
    private val defenseList: MutableList<UUID> = ArrayList()
    private val beingsCooldown: ConcurrentHashMap<UUID, Cooldown> = ConcurrentHashMap()
    private val angerCooldown: ConcurrentHashMap<UUID, Cooldown> = ConcurrentHashMap()
    private val defenseCooldown: ConcurrentHashMap<UUID, Cooldown> = ConcurrentHashMap()
    private val dissipatesCooldown: ConcurrentHashMap<UUID, Cooldown> = ConcurrentHashMap()
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            if (!event.action.name.startsWith("RIGHT")) return@runTaskAsynchronously
            val player = event.player
            val handItem = player.itemInHand
            if (handItem.type != Material.INK_SACK) return@runTaskAsynchronously
            when (ItemUtil.getInternalName(handItem).uppercase()) {
                "ANGER" -> {
                    angerCooldown.putIfAbsent(player.uniqueId, Cooldown(0L))
                    if (!angerCooldown[player.uniqueId]!!.hasExpired()) {
                        player.sendMessage(CC.translate("&c物品正在冷却中!"))
                        return@runTaskAsynchronously
                    }

                    angerCooldown[player.uniqueId] = Cooldown(2L, TimeUnit.MINUTES)
                    angerList.add(player.uniqueId)
                    player.sendMessage(CC.translate("&a你使用了 &c怒"))

                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.instance, {
                        angerList.remove(player.uniqueId)
                        player.sendMessage(CC.translate("&c怒 &7效果已结束"))
                    }, 80 * 20L)
                }

                "DEFENSE" -> {
                    defenseCooldown.putIfAbsent(player.uniqueId, Cooldown(0L))
                    if (!defenseCooldown[player.uniqueId]!!.hasExpired()) {
                        player.sendMessage(CC.translate("&c物品正在冷却中!"))
                        return@runTaskAsynchronously
                    }

                    defenseCooldown[player.uniqueId] = Cooldown(1L, TimeUnit.MINUTES)
                    defenseList.add(player.uniqueId)
                    player.sendMessage(CC.translate("&a你使用了 &9御"))

                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.instance, {
                        defenseList.remove(player.uniqueId)
                        player.sendMessage(CC.translate("&9御 &7效果已结束"))
                    }, 45 * 20L)
                }

                "BEINGS" -> {
                    beingsCooldown.putIfAbsent(player.uniqueId, Cooldown(0L))
                    if (!beingsCooldown[player.uniqueId]!!.hasExpired()) {
                        player.sendMessage(CC.translate("&c物品正在冷却中!"))
                        return@runTaskAsynchronously
                    }

                    player.sendMessage(CC.translate("&a你使用了 &6生"))
                    beingsCooldown[player.uniqueId] = Cooldown(40L, TimeUnit.SECONDS)
                    (player as CraftPlayer).handle.absorptionHearts += 20L
                }

                "DISSIPATES" -> {
                    dissipatesCooldown.putIfAbsent(player.uniqueId, Cooldown(0L))
                    if (!dissipatesCooldown[player.uniqueId]!!.hasExpired()) {
                        player.sendMessage(CC.translate("&c物品正在冷却中!"))
                        return@runTaskAsynchronously
                    }

                    dissipatesCooldown[player.uniqueId] = Cooldown(25L, TimeUnit.SECONDS)
                    player.sendMessage(CC.translate("&a你使用了 &b散"))
                    player.activePotionEffects.forEach {
                        if (!isInPotionEffectType(it.type)) return@forEach
                        player.removePotionEffect(it.type)
                    }
                }
                else -> return@runTaskAsynchronously
            }

            InvUtil.takeItemInHand(player)
        }
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            val victim = event.entity as? Player ?: return@runTaskAsynchronously
            val attacker = event.damager as? Player ?: return@runTaskAsynchronously

            var damage = event.damage

            if (angerList.contains(attacker.uniqueId)) damage *= 1.15

            if (defenseList.contains(victim.uniqueId)) damage *= 0.8

            Bukkit.getScheduler().runTask(Main.instance) { event.damage = damage }
        }
    }

    private fun isInPotionEffectType(type: PotionEffectType): Boolean {
        return type == PotionEffectType.BLINDNESS ||
                type == PotionEffectType.CONFUSION ||
                type == PotionEffectType.HUNGER ||
                type == PotionEffectType.POISON ||
                type == PotionEffectType.SLOW ||
                type == PotionEffectType.SLOW_DIGGING ||
                type == PotionEffectType.WEAKNESS ||
                type == PotionEffectType.WITHER ||
                type == PotionEffectType.HARM
    }
}