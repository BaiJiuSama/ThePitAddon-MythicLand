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

@Command(name = "buyProp")
class BuyProp {
    private val prefix = Main.instance.PREFIX

    @Execute
    fun onCommand(@Context player: Player, @Arg str: String) {
        val price = 10000
        when (str.uppercase()) {
            "ANGER" -> BuyUtil.giveItem(player, anger, price)

            "DEFENSE" -> BuyUtil.giveItem(player, defense, price)

            "BEINGS" -> BuyUtil.giveItem(player, beings, price)

            "DISSIPATES" -> BuyUtil.giveItem(player, dissipates, price)

            else -> player.sendMessage(CC.translate("$prefix&c/buyProp < ANGER / DEFENSE / BEINGS / DISSIPATES >"))
        }
    }

    private val anger: ItemStack by lazy {
        val lore= listOf(
            "&e特殊物品",
            "&7使用时自身可获得 &c+15% &7全类型伤害加成",
            "",
            "&7持续时间: &f1分20秒",
            "&7冷却时间: &f2分钟",
            "&7使用方式: &e手持右键"
        )

        val inkSack by lazy { ItemStack(Material.INK_SACK, 1, 1.toShort()) }
        ItemBuilder(inkSack).name("&f&k!!&r &c怒 &f&k!!").lore(lore).internalName("ANGER").shiny().canTrade(true).canSaveToEnderChest(true).build()
    }

    private val defense: ItemStack by lazy {
        val lore= listOf(
            "&e特殊物品",
            "&7使用时自身可获得 &9+20% &7全类型防御加成",
            "",
            "&7持续时间: &f45秒",
            "&7冷却时间: &f1分钟",
            "&7使用方式: &e手持右键"
        )

        val inkSack by lazy { ItemStack(Material.INK_SACK, 1, 4.toShort()) }
        ItemBuilder(inkSack).name("&f&k!!&r &9御 &f&k!!").lore(lore).internalName("DEFENSE").shiny().canTrade(true).canSaveToEnderChest(true).build()
    }

    private val beings: ItemStack by lazy {
        val lore= listOf(
            "&e特殊物品",
            "&7使用时自身可获得 &610❤ &7的生命吸收效果",
            "",
            "&7冷却时间: &f40秒",
            "&7使用方式: &e手持右键"
        )

        val inkSack by lazy { ItemStack(Material.INK_SACK, 1, 14.toShort()) }
        ItemBuilder(inkSack).name("&f&k!!&r &6生 &f&k!!").lore(lore).internalName("BEINGS").shiny().canTrade(true).canSaveToEnderChest(true).build()
    }

    private val dissipates: ItemStack by lazy {
        val lore= listOf(
            "&e特殊物品",
            "&7使用时清散自身所有的&cDEBUFF&7效果",
            "",
            "&7冷却时间: &f25秒",
            "&7使用方式: &e手持右键"
        )

        val inkSack by lazy { ItemStack(Material.INK_SACK, 1, 12.toShort()) }
        ItemBuilder(inkSack).name("&f&k!!&r &b散 &f&k!!").lore(lore).internalName("DISSIPATES").shiny().canTrade(true).canSaveToEnderChest(true).build()
    }
}

