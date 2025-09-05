package cn.irina.thepitaddon.command.player

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.util.chat.CC
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

@Command(name = "ping")
class Ping {
    @Execute
    fun ping(@Context player: Player) {
        val ping = (player as CraftPlayer).handle.ping
        val pingColor = when {
            ping < 50 -> "&a"
            ping < 100 -> "&e"
            ping < 150 -> "&6"
            ping < 200 -> "&c"
            else -> "&4"
        }
        player.sendMessage(CC.translate("&fYour ping is $pingColor$ping ms"))
    }
}