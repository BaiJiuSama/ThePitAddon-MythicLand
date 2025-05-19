package cn.irina.thepitaddon.command.player.buyItems

import cn.charlotte.pit.data.PlayerProfile
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 重构商店购买的逻辑
 * @author ShanguanLinG
 * @since 2025/12/5/19
 */

@Command(name = "buyStrengthenDiamondItem")
class BuyStrengthenDiamondItem {
    @Execute
    fun onDefault(@Context player: Player) {
        player.sendMessage(CC.translate("&cUsage: /buyStrengthenDiamondItem <item>"))
    }

    @Execute(name = "sword")
    fun buySword(@Context player: Player) {
        buyItem(player, getStrengthenDiamondItem(), 650)
    }

    private fun getStrengthenDiamondItem(): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&7从时空商人处获得")
        lore.add("")
        lore.add("&7死亡后消失")
        lore.add("")
        lore.add("&9攻击伤害 +8.25")
        lore.add("&9无法破坏")

        return ItemBuilder(Material.DIAMOND_SWORD)
            .shiny()
            .name("&c强化钻石剑")
            .lore(lore)
            .removeOnJoin(false)
            .deathDrop(true)
            .canTrade(true)
            .canSaveToEnderChest(true)
            .internalName("shopItem")
            .itemDamage(7.0)
            .enchantment(Enchantment.DAMAGE_ALL, 1)
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

