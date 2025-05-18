package cn.irina.thepitaddon.command.player

import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.item.ItemUtil
import cn.irina.thepitaddon.utils.NBTUtil.changeEnchantToItem
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command(name = "changeItemEnchant")
@Permission("pit.changeItemEnchant")
class ChangeItemEnchant {
//    @Execute
//    fun changeEnchant(
//        @Context myself: CommandSender,
//        @Arg target: Player,
//        @Arg needChange: String,
//        @Arg toChange: String,
//        @Arg changeLevel: Boolean
//    ) {
//        val item = target.itemInHand
//
//        if (ItemUtil.getInternalName(item) == null || item.enchantments.isEmpty()) return
//
//        val returnItem = changeEnchantToItem(item, needChange, toChange, changeLevel)
//        target.itemInHand = returnItem
//        myself.sendMessage(CC.translate("&aDONE"))
//    }
}
