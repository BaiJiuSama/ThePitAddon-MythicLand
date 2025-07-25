package cn.irina.thepitaddon.command.player

import cn.charlotte.pit.data.PlayerProfile
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.utils.Log
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.libs.core.lang.UUID
import net.mizukilab.pit.util.chat.CC
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

@Command(name = "proving")
class PlayerProving {
    private var task: BukkitRunnable? = null
    private val checkProving = hashMapOf<UUID, Int>()

    @Execute
    fun proving(@Context player: Player) {
        if (player.hasPermission("storage.use")) {
            player.sendMessage(CC.translate("&c你已经通过验证了, 无需重复验证!"))
            return
        }

        if (checkNumber.getOrDefault(player.uniqueId, 0) != 1) {
            checkNumber[player.uniqueId] = 1
            sendPromptMessage(player)
            if (PlayerProving.task == null) {
                PlayerProving.task = object : BukkitRunnable() {
                    override fun run() {
                        checkNumber.replace(player.uniqueId, 0)
                    }
                }
                (PlayerProving.task as BukkitRunnable).runTaskLater(Main.instance, 8 * 20L)
            }
        } else {
            val playerProfile = PlayerProfile.getRawCache(player.uniqueId) ?: return
            val totalPlayedTime = playerProfile.totalPlayedTime
            val totalKills = playerProfile.kills
            val prestige = playerProfile.prestige
            val days = totalPlayedTime / (24 * 60 * 60 * 1000)
            val hours = (totalPlayedTime % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
            val minutes = (totalPlayedTime % (60 * 60 * 1000)) / (60 * 1000)
            val totalPlayedTimeQualified = totalPlayedTime > (60 * 60 * 1000) * 24
            val totalKillsQualified = totalKills > 100000
            val prestigeQualified = prestige > 30
            player.sendMessage(CC.translate("&7你当前游玩时间为: &a${days}天${hours}小时${minutes}分钟"))
            if (!totalPlayedTimeQualified) {
                player.sendMessage(CC.translate("&c你没有达到24小时的游戏时长要求!"))
                return
            }
            player.sendMessage(CC.translate("&7你当前的总击杀数是: &c${totalKills}"))
            if (!totalKillsQualified) {
                player.sendMessage(CC.translate("&c你没有达到100000击杀数的要求!"))
                return
            }
            player.sendMessage(CC.translate("&7你当前的精通是: &b${prestige}"))
            if (!prestigeQualified) {
                player.sendMessage(CC.translate("&c你没有满足精通XXX的要求!"))
                return
            }
            player.sendMessage(CC.translate(""))
            player.sendMessage(CC.translate("&a你已获得认证."))

            Log.send("&e控制台执行: &fstorage.use 权限给予")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp u ${player.displayName} p set storage.use")
        }
    }

    private fun sendPromptMessage(@Context player: Player) {
        player.sendMessage(CC.translate(""))
        player.sendMessage(CC.translate("&c注意! 你当前正在使用 &e/proving &c指令认证!"))
        player.sendMessage(CC.translate(""))
        player.sendMessage(CC.translate("&c需要满足以下条件: "))
        player.sendMessage(CC.translate("&e■ &7总游玩时间 到达 &e24 小时"))
        player.sendMessage(CC.translate("&e■ &7总击杀 到达 &e10000"))
        player.sendMessage(CC.translate("&e■ &7精通 到达 &eXXX"))
        player.sendMessage(CC.translate(""))
        player.sendMessage(CC.translate("&c请在 &e8s &c内再次输入 &e/proving &c开始认证."))
        player.sendMessage(CC.translate(""))
        player.sendMessage(CC.translate("&c通过验证后, 您就可以使用 &e/save &c指令保存您的装备了。"))
        player.sendMessage(CC.translate(""))
    }

    companion object {
        private val checkNumber = HashMap<java.util.UUID, Int>()
        private var task: BukkitRunnable? = null
    }
}