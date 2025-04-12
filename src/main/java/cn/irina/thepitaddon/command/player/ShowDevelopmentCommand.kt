package cn.irina.thepitaddon.command.player

import cn.charlotte.pit.util.chat.CC
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.execute.Execute
import org.bukkit.entity.Player

@Command(name = "thepit")
class ShowDevelopmentCommand {

    @Execute
    fun development(player: Player) {
        player.sendMessage(CC.translate("&7&lᴘᴏᴡᴇʀᴇᴅ &7&lʙʏ &bɪ&fʀɪɴᴀ"))
    }
}
