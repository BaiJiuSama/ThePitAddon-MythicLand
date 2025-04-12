package cn.irina.thepitaddon.runnable

import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.util.chat.CC
import cn.irina.thepitaddon.ThePitAddon
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
        private val message: String = ThePitAddon.instance.config.getString("FreeCoinAndExperience.Message")
        private val experience = ThePitAddon.instance.config.getInt("FreeCoinAndExperience.Experience")
        private val coin = ThePitAddon.instance.config.getInt("FreeCoinAndExperience.Coin")
    }
}