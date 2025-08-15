package cn.irina.thepitaddon.command.player

import cn.irina.thepitaddon.Main
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
        var item = player.inventory.itemInHand
        if ("Gem" == gemTypeName) {
            var newItem = ItemBuilder(item).changeNbt("boostedByGem", 0).build()
            player.inventory.itemInHand = newItem
            player.updateInventory()
            return
        }
        if ("GlobalGem" == gemTypeName) {
            var newItem = ItemBuilder(item).changeNbt("boostedByGlobalGem", 0).build()
            player.inventory.itemInHand = newItem
            player.updateInventory()
            return
        }
    }
}
