package cn.irina.thepitaddon.command.admin

import net.mizukilab.pit.util.chat.CC
import cn.irina.thepitaddon.utils.NBTUtil.addEnchantToItem
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command(name = "enchant")
@Permission("pit.admin")
class AdminCommandEnchant {
//    @Execute
//    fun enchant(
//        @Context player: Player,
//        @Arg enchantNBT: String,
//        @Arg enchantLevel: Int
//    ) {
//        val item = addEnchantToItem(player.itemInHand, enchantNBT, enchantLevel)
//        player.inventory.itemInHand = item
//        player.sendMessage(CC.translate("&aSUCCESS!"))
//    }
//
//    @Execute(name = "toTarget")
//    fun enchantToTarget(
//        @Context sender: CommandSender,
//        @Arg target: Player,
//        @Arg enchantNBT: String,
//        @Arg enchantLevel: Int
//    ) {
//        val item = addEnchantToItem(target.itemInHand, enchantNBT, enchantLevel)
//        target.inventory.itemInHand = item
//        sender.sendMessage(CC.translate("&aSUCCESS!"))
//    }
}
