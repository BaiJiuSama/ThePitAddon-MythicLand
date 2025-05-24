package cn.irina.thepitaddon.command.player.buyItems

import cn.charlotte.pit.data.PlayerProfile
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


@Command(name = "buyEntityCoin")
class BuyEntityCoin {
    @Execute
    fun onDefault(@Context player: Player) {
        player.sendMessage(CC.translate("&cUsage: /buyEntityCoin <1k/1w/10w/100w>"))
    }

    @Execute(name = "1k")
    fun buy1kEntityCoin(@Context player: Player) {
        BuyUtil.giveItem(player, getEntityCoin(1000, "&6一千硬币"), 1000)
    }

    @Execute(name = "1w")
    fun buy1wEntityCoin(@Context player: Player) {
        BuyUtil.giveItem(player, getEntityCoin(10000, "&6一万硬币"), 10000)
    }

    @Execute(name = "10w")
    fun buy10wEntityCoin(@Context player: Player) {
        BuyUtil.giveItem(player, getEntityCoin(100000, "&6十万硬币"), 100000)
    }

    @Execute(name = "100w")
    fun buy100wEntityCoin(@Context player: Player) {
        BuyUtil.giveItem(player, getEntityCoin(1000000, "&6一百万硬币"), 1000000)
    }

    private fun getEntityCoin(amount: Int, name: String): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&7实体货币")
        lore.add("")
        lore.add("&7在神话天坑内广泛流通")
        lore.add("&7可用于兑换购买一些物品")

        return ItemBuilder(Material.GOLD_INGOT)
            .name(name)
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(false)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("coin")
            .build()
    }
}