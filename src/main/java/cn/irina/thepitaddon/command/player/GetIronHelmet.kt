package cn.irina.thepitaddon.command.player

import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.util.chat.CC
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.execute.Execute
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Command(name = "getironhelmet")
class GetIronHelmet {

    @Execute
    fun getIronHelmet(player: Player) {
        val profile = PlayerProfile.getRawCache(player.uniqueId) ?: return

        if (profile.coins < 200) {
            player.sendMessage(CC.translate("&c你没有足够的金币!"))
            return
        }

        profile.coins -= 200

        player.inventory.addItem(ItemStack(Material.IRON_HELMET))
    }
}
