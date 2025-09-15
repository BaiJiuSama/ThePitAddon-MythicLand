package cn.irina.thepitaddon.command.admin

import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.runnable.Maintenance
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import net.mizukilab.pit.util.chat.CC
import org.bukkit.entity.Player

@Command(name = "lock")
@Permission("pit.admin")
class LockCommand {
    @Execute
    fun handleLockCommand(@Context player: Player) {
        val prefix = Main.instance.PREFIX
        val newMode = !Maintenance.isInMaintenanceMode()
        Maintenance.setMaintenanceMode(newMode)
        if (newMode) {
            player.sendMessage(CC.translate("$prefix&a已启动维护模式."))
        } else {
            player.sendMessage(CC.translate("$prefix&c已关闭维护模式."))
        }
    }
}