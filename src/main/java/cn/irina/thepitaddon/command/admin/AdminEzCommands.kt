package cn.irina.thepitaddon.command.admin

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import net.mizukilab.pit.getPitProfile
import net.mizukilab.pit.util.chat.CC
import org.bukkit.entity.Player

@Command(name = "heal")
@Permission("pit.admin")
class AdminHealSelf {
    @Execute
    fun heal(@Context player: Player) {
        player.health = player.maxHealth
        player.foodLevel = 20
        player.sendMessage(CC.translate("&aSUCCESS."))
    }
}

@Command(name = "clearBounty")
@Permission("pit.admin")
class AdminClearBounty {
    @Execute
    fun clearBounty(@Context player: Player) {
        val pitProfile = player.getPitProfile()
        pitProfile.bounty = 0
        player.sendMessage(CC.translate("&aSUCCESS."))
    }
}

