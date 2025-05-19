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

@Command(name = "buyIronHelmet")
class BuyIronHelmet {
    @Execute
    fun buyIronHelmet(@Context player: Player) {
        buyItem(player, getIronHelmet(), 100)
    }

    private fun getIronHelmet(): ItemStack {
        val lore: MutableList<String> = ArrayList()
        lore.add("&6从时空商人处获得")
        return ItemBuilder(Material.IRON_HELMET)
            .name("&f铁头盔")
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(true)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("shopItem")
            .buildWithUnbreakable()
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
