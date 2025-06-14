package cn.irina.thepitaddon.command.admin

import cn.charlotte.pit.ThePit
import net.mizukilab.pit.util.chat.CC
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
    @Execute
    fun onCommand(
        @Context sender: CommandSender,
        @Arg str: String,
        @Arg level: Int
    ) {
        val player = sender as? Player ?: return
        sender.sendMessage(CC.translate("&aSUCCESS!"))
        val item = onEnchant(player.itemInHand, str, level)
        player.itemInHand = item
    }

    @Execute(name = "toTarget")
    fun onCommand(
        @Context sender: CommandSender,
        @Arg target: Player,
        @Arg str: String,
        @Arg level: Int
    ) {
        sender.sendMessage(CC.translate("&aSUCCESS!"))
        val item = onEnchant(target.itemInHand, str, level)
        target.itemInHand = item
    }

    fun onEnchant(item: ItemStack, name: String, level: Int): ItemStack? {
        val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(item)
        val enchant = ThePit.getInstance().enchantmentFactor.enchantmentMap[name] ?: return null
        val oldMap = pitItem.enchantments.apply { put(enchant, level) }
        pitItem.enchantments = oldMap
        return pitItem.toItemStack()
    }
}
