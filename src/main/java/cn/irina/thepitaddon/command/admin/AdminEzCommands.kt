package cn.irina.thepitaddon.command.admin

import cn.charlotte.pit.data.PlayerProfile
import dev.rollczi.litecommands.annotations.argument.Arg
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

@Command(name = "fixWipe")
@Permission("pit.admin")
class FixWipe {
    @Execute
    fun onCommand(@Context sender: Player, @Arg player: Player) {
        if (!player.isOnline) {
            sender.sendMessage(CC.translate("&c目标玩家并不在线"))
            return
        }

        val profile = PlayerProfile.getRawCache(player.uniqueId)
        if (profile.wipedData != null) {
            profile.wipedData.isKnow = true
            player.sendMessage(CC.translate("&aSuccess."))
        }
    }
}

