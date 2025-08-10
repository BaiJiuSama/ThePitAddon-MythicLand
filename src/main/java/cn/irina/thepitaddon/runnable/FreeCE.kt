package cn.irina.thepitaddon.runnable
import cn.charlotte.pit.data.PlayerProfile
import cn.irina.thepitaddon.Main
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.MessageType
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.level.LevelUtil
import net.mizukilab.pit.util.rank.RankUtil
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

class FreeCE : Runnable {
    override fun run() {
        val limit = 100
        Bukkit.getOnlinePlayers().toList().forEach { player ->
            try {
                val profile = PlayerProfile.getRawCache(player.uniqueId)
                val exp = profile.experience
                if (!player.world.name.equals("afk")) return
                if (profile.prestige >= limit && profile.level >= 120) {
                    player.sendMessage(CC.translate("&b&l经验值已满! &7您已满级, 无法继续升级!"))
                    return
                }
                if (profile.level >= 120) {
                    autoPrestige(player)
                    return
                }
                val rewardExp = getRewardExp(profile.prestige)
                profile.experience = exp + rewardExp
                player.sendMessage(CC.translate(message).replace("%exp%", rewardExp.toInt().toString()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRewardExp(prestige: Int): Double {
        var booster = 0.5
        if (prestige <= 12) {
            booster = 0.36
        } else if (prestige <= 20) {
            booster = 0.30
        } else if (prestige <= 30) {
            booster = 0.24
        } else if (prestige <= 50) {
            booster = 0.22
        }
        var needExp = 0.2
        for (level in 1..120) {
            needExp += LevelUtil.getLevelExpRequired(prestige, level)
        }
        return needExp * booster + 10000
    }

    private fun autoPrestige(player: Player) {
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        val data = profile.unlockedPerkMap["FastPass"]
        //FastPass Perk
        if (data != null) {
            profile.experience = LevelUtil.getLevelTotalExperience(profile.getPrestige(), 50)
        }
        profile.coins = 0.0
        profile.experience = 0.0
        profile.grindedCoins = 0.0
        profile.setPrestige(profile.getPrestige() + 1)
        //PlayerUtil.clearPlayer(player);
        //InventoryUtil.supplyItems(player);
        var award = 10
        if (profile.getPrestige() > 4) {
            award += 10
        }
        if (profile.getPrestige() > 10) {
            award += 10
        }
        if (profile.getPrestige() > 14) {
            award += 10
        }
        profile.renown += award
        CC.boardCast(
            MessageType.PRESTIGE,
            (("&e&l精通! &7" + RankUtil.getPlayerColoredName(player.name)).toString() + " &7解锁了精通 &e" + RomanUtil.convert(
                profile.getPrestige()
            )).toString() + " &7,gg!"
        )
        player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 1f, 1f)
    }

    companion object {
        private val message: String = Main.instance.config.getString("FreeCoinAndExperience.Message")
//        private val experience = Main.instance.config.getInt("FreeCoinAndExperience.Experience")
//        private val coin = Main.instance.config.getInt("FreeCoinAndExperience.Coin")
    }
}