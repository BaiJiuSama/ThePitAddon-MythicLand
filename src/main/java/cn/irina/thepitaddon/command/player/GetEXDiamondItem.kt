package cn.irina.thepitaddon.command.player

import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.item.ItemBuilder
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.execute.Execute
import org.bukkit.Material
import org.bukkit.entity.Player

@Command(name = "getexdiamonditem")
class GetEXDiamondItem {
    @Execute
    fun getExDiamondItem(player: Player) {
        val profile = PlayerProfile.getRawCache(player.uniqueId) ?: return

        if (profile.coins < 1000) {
            player.sendMessage(CC.translate("&c你没有足够的金币!"))
            return
        }

        profile.coins -= 1000

        val lore: MutableList<String> = ArrayList()

        lore.add("&6从神秘人处获得")
        lore.add("")
        lore.add("&7浸泡过 &6赏金溶剂 &7后的利刃")
        lore.add("&7将对持有赏金的玩家造成 &c+30% &7的伤害")
        lore.add("")
        lore.add("&7死亡后消失")
        lore.add("")
        lore.add("&9攻击伤害 +8")
        lore.add("&9无法破坏")

        player.inventory.addItem(
            ItemBuilder(Material.DIAMOND_SWORD)
                .shiny()
                .name("&c强化钻石剑")
                .lore(lore)
                .removeOnJoin(false)
                .deathDrop(true)
                .canTrade(true)
                .canSaveToEnderChest(true)
                .internalName("EXDiamondSword")
                .itemDamage(8.0)
                .buildWithUnbreakable()
        )
    }
}

