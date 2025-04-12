package cn.irina.thepitaddon.command.admin

import net.mizukilab.pit.util.chat.CC
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.entity.Player

@Command(name = "heal")
@Permission("pit.admin")
class AdminHealSelf {
    @Execute
    fun heal(@Context player: Player) {
        player.health = player.maxHealth
        player.foodLevel = 20
        player.sendMessage(CC.translate("&aHEALTH!"))
    }
}
