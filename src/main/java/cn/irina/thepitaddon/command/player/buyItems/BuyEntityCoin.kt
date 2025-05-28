package cn.irina.thepitaddon.command.player.buyItems

import cn.irina.thepitaddon.Main
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Command(name = "buyEntityCoin")
class BuyEntityCoin {
    private val prefix = Main.instance.PREFIX

    @Execute
    fun onCommand(@Context player: Player, @Arg str: String) {
        when (str.uppercase()) {
            "1k" -> BuyUtil.giveItem(player, physicalCoin(1), 1000)

            "1w" -> BuyUtil.giveItem(player, physicalCoin(2), 10000)

            "10w" -> BuyUtil.giveItem(player, physicalCoin(3), 100000)

            "100w" -> BuyUtil.giveItem(player, physicalCoin(4), 1000000)

            "1kw" -> BuyUtil.giveItem(player, physicalCoin(5), 10000000)

            else -> player.sendMessage(CC.translate("$prefix&c/buyPhysicalCoin < 1k / 1w / 10w / 100w / 1kw >"))
        }
    }

    private val lore: MutableList<String> by lazy {ArrayList(listOf(
        "&7实体货币",
        "",
        "&7在神话天坑内广泛流通",
        "&7可用于兑换购买一些物品"
    ))}

    private fun physicalCoin(i: Int): ItemStack {
        val name: String = when (i) {
            1 -> "&6一千"
            2 -> "&6一万"
            3 -> "&6十万"
            4 -> "&6一百万"
            5 -> "&6一千万"
            else -> "&c非法的"
        }

        return ItemBuilder(Material.GOLD_INGOT)
            .name("&f&k!!&r $name&r &e硬币 &f&k!!&r")
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(false)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("coin")
            .build()
    }
}