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
        buyItem(player, get1kEntityCoin(), 1000)
    }

    @Execute(name = "1w")
    fun buy1wEntityCoin(@Context player: Player) {
        buyItem(player, get1wEntityCoin(), 10000)
    }

    @Execute(name = "10w")
    fun buy10wEntityCoin(@Context player: Player) {
        buyItem(player, get10wEntityCoin(), 100000)
    }

    @Execute(name = "100w")
    fun buy100wEntityCoin(@Context player: Player) {
        buyItem(player, get100wEntityCoin(), 1000000)
    }

    private fun get1kEntityCoin(): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&7实体货币")
        lore.add("")
        lore.add("&7在神话天坑内广泛流通")
        lore.add("&7可用于兑换购买一些物品")

        return ItemBuilder(Material.GOLD_INGOT)
            .name("&6一千硬币")
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(false)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("coin")
            .build()
    }

    private fun get1wEntityCoin(): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&7实体货币")
        lore.add("")
        lore.add("&7在神话天坑内广泛流通")
        lore.add("&7可用于兑换购买一些物品")

        return ItemBuilder(Material.GOLD_INGOT)
            .name("&6一万硬币")
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(false)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("coin")
            .build()
    }

    private fun get10wEntityCoin(): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&7实体货币")
        lore.add("")
        lore.add("&7在神话天坑内广泛流通")
        lore.add("&7可用于兑换购买一些物品")

        return ItemBuilder(Material.GOLD_INGOT)
            .name("&6十万硬币")
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(false)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("coin")
            .build()
    }

    private fun get100wEntityCoin(): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&7实体货币")
        lore.add("")
        lore.add("&7在神话天坑内广泛流通")
        lore.add("&7可用于兑换购买一些物品")

        return ItemBuilder(Material.GOLD_INGOT)
            .name("&6一百万硬币")
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(false)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("coin")
            .build()
    }

    private fun buyItem(player: Player, item: ItemStack, price: Int) {
        val profile = PlayerProfile.getRawCache(player.uniqueId) ?: return
        if (profile.coins < price) {
            player.sendMessage(CC.translate("&c你的硬币不足!"))
            return
        }
        profile.coins -= price
        player.playSound(player.location, Sound.NOTE_PLING, 1f, 1f)
        player.inventory.addItem(item)
        player.sendMessage(CC.translate("&a购买成功!"))
    }
}