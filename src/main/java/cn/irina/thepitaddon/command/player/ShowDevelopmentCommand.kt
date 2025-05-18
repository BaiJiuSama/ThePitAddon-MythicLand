package cn.irina.thepitaddon.command.player

import net.mizukilab.pit.util.chat.CC
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import org.bukkit.command.CommandSender

@Command(name = "thepit")
class ShowDevelopmentCommand {

    @Execute
    fun development(@Context sender: CommandSender) {
        sender.sendMessage(CC.translate("&bIrina&fThePitAddon"))
        sender.sendMessage(CC.translate("&aPower By _Ir1na_, ShanguanLinG."))
    }
}
