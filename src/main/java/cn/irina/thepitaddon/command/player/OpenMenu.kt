package cn.irina.thepitaddon.command.player

import cn.charlotte.pit.ThePit
import cn.irina.thepitaddon.Main
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.menu.perk.normal.choose.PerkChooseMenu
import net.mizukilab.pit.menu.prestige.PrestigeMainMenu
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/*
 * @Author Irina
 * @Date 2025/7/24 14:51
 */

@Command(name = "om")
class OpenMenu {
    private val pitApi = ThePit.getApi()
    private val prefix = Main.instance.PREFIX

    private val cd = ConcurrentHashMap<UUID, Cooldown>()
    private val cooldown = 30L

    @Execute
    fun handleCommand(player: Player, @Arg menuName: String) {
        if (player.hasPermission("pit.admin")) handleOpenMenu(player, menuName)

        val cd0: Cooldown = cd[player.uniqueId] ?: Cooldown(0L)
        if (!cd0.hasExpired()) {
            val lastCd = TimeUnit.MILLISECONDS.toSeconds(cd0.duration)
            player.sendMessage(CC.translate("$prefix&c冷却中, 剩余 $lastCd &c秒..."))
            return
        }

        cd[player.uniqueId] = Cooldown(cooldown, TimeUnit.SECONDS)
        handleOpenMenu(player, menuName)
    }

    private fun handleOpenMenu(player: Player, menu: String) {
        when (menu.uppercase()) {
            "SHOP" -> pitApi.openMenu(player, "shop")
            "S" -> pitApi.openMenu(player, "shop")
            "PRESTIGE" -> PrestigeMainMenu().openMenu(player)
            "PRE" -> PrestigeMainMenu().openMenu(player)
            "P" -> PrestigeMainMenu().openMenu(player)
            "PERK" -> PerkChooseMenu().openMenu(player)
            "PE" -> PerkChooseMenu().openMenu(player)

            else -> player.sendMessage(CC.translate("$prefix&c错误的菜单名称!"))
        }
    }
}