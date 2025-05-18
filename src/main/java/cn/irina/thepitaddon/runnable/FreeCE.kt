package cn.irina.thepitaddon.runnable

import cn.charlotte.pit.data.PlayerProfile
import net.mizukilab.pit.util.chat.CC
import cn.irina.thepitaddon.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FreeCE : Runnable {
    override fun run() {
        Bukkit.getOnlinePlayers().forEach { player: Player? ->
            val profile = PlayerProfile.getRawCache(player!!.uniqueId)
            val coins = profile.coins
            val exp = profile.experience
            profile.coins = coins + coin
            profile.experience = exp + experience
            player.sendMessage(CC.translate(message))
        }
    }

    companion object {
        private val message: String = Main.instance.config.getString("FreeCoinAndExperience.Message")
        private val experience = Main.instance.config.getInt("FreeCoinAndExperience.Experience")
        private val coin = Main.instance.config.getInt("FreeCoinAndExperience.Coin")
    }
}