package cn.irina.thepitaddon.command.player

import cn.irina.thepitaddon.Main
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.item.ItemBuilder
import net.mizukilab.pit.util.item.ItemUtil
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/*
 * @Author Irina, ShanguanLinG
 * @Date 2025/6/19
 */

@Command(name = "unGem")
class UnGem {
    val prefix = Main.instance.PREFIX

    @Execute
    fun onCommand(
        @Context sender: CommandSender,
        @Arg gemTypeName: String,
        @Arg player: Player
    ) {
        val item = player.inventory.itemInHand
        if (item == null || item.type == Material.AIR) {
            sender.sendMessage(CC.translate("$prefix &c请手持物品!"))
            return
        }
        if ("Gem" == gemTypeName) {
            if (!isBoostedByGem(item)) {
                sender.sendMessage(CC.translate("$prefix &c这件物品上并没有使用过 &a遵纪守法的宝石!"))
                return
            }
            val newItem = ItemBuilder(item).changeNbt("boostedByGem", 0).build()
            player.inventory.itemInHand = newItem
            player.updateInventory()
            player.sendMessage(CC.translate("$prefix &a成功移除了 &a遵纪守法的宝石 &7点缀."))
            return
        }
        if ("GlobalGem" == gemTypeName) {
            if (!isBoostedByGlobalGem(item)) {
                sender.sendMessage(CC.translate("$prefix &c这件物品上并没有使用过 &b举世瞩目的宝石!"))
                return
            }
            val newItem = ItemBuilder(item).changeNbt("boostedByGlobalGem", 0).build()
            player.inventory.itemInHand = newItem
            player.updateInventory()
            player.sendMessage(CC.translate("$prefix &a成功移除了 &b举世瞩目的宝石 &7点缀."))
            return
        }
    }

    private fun isBoostedByGem(item: ItemStack?): Boolean {
        val itemStringData = ItemUtil.getItemBoolData(item, "boostedByGem")
        return itemStringData
    }

    private fun isBoostedByGlobalGem(item: ItemStack?): Boolean {
        val itemStringData = ItemUtil.getItemBoolData(item, "boostedByGlobalGem")
        return itemStringData
    }
}
