package cn.irina.thepitaddon.events

import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.event.PitKillEvent
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.ChatComponentBuilder
import cn.irina.thepitaddon.ThePitAddon
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.Configuration
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PlayerListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.displayName.equals("Emptyirrn".uppercase())) player.kickPlayer(CC.translate("$PREFIX&c您已被禁止登录于本服天坑乱斗"))
        if (player.hasMetadata("NPC")) return
        Bukkit.getScheduler().runTaskAsynchronously(ThePitAddon.instance) {
            event.joinMessage = null
            if (!player.hasPermission("pit.admin")) {
                if (player.hasPermission("pit.support")) {
                    Bukkit.broadcastMessage(CC.translate("&8[&a+&8] &6" + player.displayName))
                } else {
                    Bukkit.broadcastMessage(CC.translate("&8[&a+&8] &7" + player.displayName))

                }
            } else {
                CC.boardCastWithPermission("&c&l注意! &7管理员 &f" + player.name + " &7加入了游戏!", "pit.admin")
                Bukkit.getConsoleSender().sendMessage(CC.translate("&c&l注意! &7管理员 &f" + player.name + " &7加入了游戏!"))
            }

            val permissionPrefix = "irina.booster."
            var recoverCount = 0
            for (permissions in player.effectivePermissions) {
                val permission = permissions.permission

                if (!permission.startsWith(permissionPrefix)) continue

                recoverCount++
                player.sendMessage(CC.translate("$PREFIX&7第 $recoverCount 次 Booster 校正检测"))
                try {
                    val count = permission.substring(permissionPrefix.length).toInt()
                    if (count > killRewardAdd.getOrDefault(
                            player.uniqueId,
                            1
                        )
                    ) killRewardAdd[player.uniqueId] =
                        count
                } catch (_: NumberFormatException) {
                    player.sendMessage(CC.translate("$PREFIX&c在你的 Booster 权限中有无法读取的数据, 请将此报告截图发送至管理员!"))
                }
            }
            if (killRewardAdd.getOrDefault(player.uniqueId, 1) > 1)
                player.sendMessage(
                CC.translate(
                    PREFIX + "&7校正成功, 当前 Booster 倍率: &a" + killRewardAdd[player.uniqueId] + " 倍"
                )
            )
        }

        if (!config.getBoolean("JoinMessage.Enable")) return
        object : BukkitRunnable() {
            override fun run() {
                object : BukkitRunnable() {
                    val totalMessages: Int = config.getStringList("JoinMessage.Messages").size

                    override fun run() {
                        if (!player.isOnline) {
                            cancel()
                            return
                        }

                        player.playSound(player.location, "note.hat", 3f, 1f)

                        val currentMessageCount = messageCount.getOrDefault(player.uniqueId, 0)

                        if (currentMessageCount == totalMessages) {
                            cancel()
                            return
                        }

                        var message = config.getStringList("JoinMessage.Messages")[currentMessageCount]

                        if (message.contains("\$PlayerName")) message = message.replace("\$PlayerName", player.name)
                        if (message.contains("\$PlayerDisplayName")) message =
                            message.replace("\$PlayerName", player.displayName)

                        player.sendMessage(CC.translate(PREFIX + message))

                        messageCount[player.uniqueId] =
                            currentMessageCount + 1
                    }
                }.runTaskTimerAsynchronously(ThePitAddon.instance, 0L, 20L)
            }
        }.runTaskLaterAsynchronously(ThePitAddon.instance, 2 * 20L)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerLeave(event: PlayerQuitEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(ThePitAddon.instance) {
            val player = event.player
            event.quitMessage = null

            if (killRewardAdd[player.uniqueId] != null) killRewardAdd.remove(
                player.uniqueId
            )
            if (!player.hasPermission("pit.admin")) {
                if (player.hasPermission("pit.support")) {
                    Bukkit.broadcastMessage(CC.translate("&8[&c-&8] &6" + player.displayName))
                } else {
                    Bukkit.broadcastMessage(CC.translate("&8[&c-&8] &7" + player.displayName))
                }
            } else {
                CC.boardCastWithPermission("&c&l注意! &7管理员 &f" + player.name + " &7退出了游戏!", "pit.admin")
                Bukkit.getConsoleSender()
                    .sendMessage(CC.translate("&c&l注意! &7管理员 &f" + player.name + " &7退出了游戏!"))
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPitKill(event: PitKillEvent) {
        val killer = event.killer

        if (killRewardAdd.getOrDefault(killer.uniqueId, 1) <= 1) return

        val newExp = event.exp * killRewardAdd.getOrDefault(killer.uniqueId, 1)

        val profile = PlayerProfile.getRawCache(killer.uniqueId)

        profile.experience += newExp
        profile.applyExperienceToPlayer(killer)

        event.exp = newExp
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleDeath(event: PlayerDeathEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(ThePitAddon.instance) {
            val player = event.entity
            if (player.hasMetadata("NPC")) return@runTaskAsynchronously

            val killer = player.killer ?: return@runTaskAsynchronously

            val pp =
                if (PlayerProfile.getRawCache(player.uniqueId) == null) null else PlayerProfile.getRawCache(
                    player.uniqueId
                )
            val kp =
                if (PlayerProfile.getRawCache(killer.uniqueId) == null) null else PlayerProfile.getRawCache(
                    killer.uniqueId
                )
            if (pp == null || kp == null) return@runTaskAsynchronously

            val itemInHand = killer.inventory.itemInHand
            val itemInLegging = killer.inventory.leggings

            val victimName = pp.formattedNameWithRoman
            val killerName = kp.formattedNameWithRoman

            val itemName =
                getItemDisplayName(itemInHand)

            val mythicWeaponNbt =
                getItemNBT(itemInHand)
            val mythicLeggingNbt =
                getItemNBT(itemInLegging)

            val mythicWeapon =
                arrayOf<BaseComponent>(TextComponent(mythicWeaponNbt))
            val mythicLegging =
                arrayOf<BaseComponent>(TextComponent(mythicLeggingNbt))

            val mythicLeggingHover = HoverEvent(HoverEvent.Action.SHOW_ITEM, mythicLegging)
            val mythicWeaponHover = HoverEvent(HoverEvent.Action.SHOW_ITEM, mythicWeapon)

            val message =
                CC.translate("&b&l击杀!&7 $victimName &7被 $killerName &7用 $itemName&7 狠狠的蹂躏了!")

            val legging = CC.translate(" &f[&6护腿&f] ")
            val weapon = CC.translate(" &f[&6武器&f] ")

            val leggingHover =
                ChatComponentBuilder(legging).setCurrentHoverEvent(mythicLeggingHover).create()
            val weaponHover =
                ChatComponentBuilder(weapon).setCurrentHoverEvent(mythicWeaponHover).create()

            Bukkit.broadcastMessage(message)
            val msg =
                ChatComponentBuilder(CC.translate("&7击杀者装备: ")).append(leggingHover).append(weaponHover)
                    .create()
            for (p in Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("irina.deathCheck")) continue
                p.spigot().sendMessage(*msg)
            }
            player.sendMessage(
                CC.translate(
                    "$PREFIX$killerName &7在你死亡前剩余的血量: &c" + String.format(
                        "%.1f",
                        killer.health * 0.5
                    ) + "❤"
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            if (damageValidRange < 0) return

            event.damage *= damageValidRange //暗改，让伤害只生效设定的数值
        }
    }

    companion object {
        private val plugin: ThePitAddon = ThePitAddon.instance
        private val config: Configuration = plugin.config

        private val killRewardAdd = ConcurrentHashMap<UUID, Int>()
        private val messageCount = ConcurrentHashMap<UUID, Int>()

        private const val PREFIX = ThePitAddon.PREFIX

        private val damageValidRange =
            if (plugin.config.getBoolean("DamageValidRange.Enable")) ThePitAddon.instance.config.getDouble("DamageValidRange.Amount") else 1.0

        private fun getItemNBT(item: ItemStack?): String {
            if (item == null || item.type == Material.AIR) return Material.AIR.toString()
            val nmsItem = CraftItemStack.asNMSCopy(item)
            val tag = NBTTagCompound()
            nmsItem.save(tag)
            return tag.toString()
        }

        private fun getItemDisplayName(item: ItemStack): String {
            val meta = item.itemMeta
            return if (meta != null && meta.hasDisplayName()) meta.displayName else item.type.name
        }
    }
}
