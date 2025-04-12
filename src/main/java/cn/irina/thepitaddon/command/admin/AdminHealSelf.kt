package cn.irina.thepitaddon.command.admin

import cn.charlotte.pit.util.chat.CC
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.entity.Player

@Command(name = "heal")
@Permission("pit.admin")
class AdminHealSelf {
    @Execute
    fun heal(player: Player) {
        player.health = player.maxHealth
        player.foodLevel = 20
        player.sendMessage(CC.translate("&aHEALTH!"))
    }
}
