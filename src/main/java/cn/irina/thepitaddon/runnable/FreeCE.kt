package cn.irina.thepitaddon.runnable

import cn.charlotte.pit.data.PlayerProfile
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.PitItem
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.MessageType
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.item.ItemUtil
import net.mizukilab.pit.util.level.LevelUtil
import net.mizukilab.pit.util.rank.RankUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class FreeCE : Runnable {
    override fun run() {
        Bukkit.getOnlinePlayers().toList().forEach { player ->
            if (player.world.name.equals("afk")) {
                val profile = PlayerProfile.getRawCache(player.uniqueId)
                val exp = profile.experience
                giveKillCounts(player, 250)
                var count = Random.nextInt(3) + 1
                if (equippedInterstellarHelmet(player)) count += 1
                if (count != 0) {
                    giveAfkFragment(player, count)
                }
                val coin = Random.nextInt(500000) + 500000
                if (equippedChainMailGoldArmor(player)) giveCoin(player, coin)
                if (isFullLevel(profile, 100, player)) return@forEach
                if (profile.level >= 120) {
                    autoPrestige(player)
                    return@forEach
                }
                val rewardExp = getRewardExp(profile.prestige)
                profile.experience = exp + rewardExp
                player.sendMessage(CC.translate(getEXPMessage).replace("%exp%", rewardExp.toInt().toString()))
            }
        }
    }

    private fun giveCoin(player: Player, coin: Int) {
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        profile.coins += coin
        player.sendMessage(CC.translate(getCoinMessage).replace("%coin%", coin.toString()))
    }

    private fun isFullLevel(
        profile: PlayerProfile,
        limit: Int,
        player: Player
    ): Boolean {
        if (profile.prestige >= limit && profile.level >= 120) {
            player.sendMessage(CC.translate("&b&l经验值已满! &7您已满级, 无法继续升级!"))
            return true
        }
        return false
    }

    private fun giveKillCounts(player: Player, kills: Int) {
        val playerProfile = PlayerProfile.getRawCache(player.uniqueId) ?: return
        playerProfile.kills += kills
        player.sendMessage(CC.translate(getKillsMessage).replace("%kills%", kills.toString()))
    }

    private fun giveAfkFragment(player: Player, count: Int) {
        val pitItem = PitItem()
        for (i in 0 until count) {
            player.inventory.addItem(pitItem.afkFragment)
        }
        player.sendMessage(CC.translate(getItemMessage).replace("%count%", count.toString()))
    }

    private fun getRewardExp(prestige: Int): Double {
        var booster = 0.24              // 50->79
        if (prestige <= 12) {           // 50->99
            booster = 0.48
        } else if (prestige <= 20) {    // 50->92
            booster = 0.40
        } else if (prestige <= 30) {    // 50->88
            booster = 0.36
        } else if (prestige <= 40) {    // 50->85
            booster = 0.32
        }
        var needExp = 0.0
        for (level in 1..120) {
            needExp += LevelUtil.getLevelExpRequired(prestige, level)
        }
        return needExp * booster + 10000
    }

    private fun isChainMailGoldArmor(chestPlate: ItemStack): Boolean {
        return ItemUtil.getInternalName(chestPlate).equals("chain-mail_gold_armor")
    }

    private fun equippedChainMailGoldArmor(player: Player): Boolean {
        if (player.inventory.chestplate == null || player.inventory.chestplate.type == Material.AIR) return false
        val chestPlate = player.inventory.chestplate
        return isChainMailGoldArmor(chestPlate)
    }

    private fun isInterstellarHelmet(helmet: ItemStack): Boolean {
        return ItemUtil.getInternalName(helmet).equals("interstellar_helmet")
    }

    private fun equippedInterstellarHelmet(player: Player): Boolean {
        if (player.inventory.helmet == null || player.inventory.helmet.type == Material.AIR) return false
        val helmet = player.inventory.helmet
        return isInterstellarHelmet(helmet)
    }

    private fun autoPrestige(player: Player) {
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        if (equippedChainMailGoldArmor(player)) {
            profile.coins /= 2
        } else {
            profile.coins = 0.0
        }
        profile.experience = 0.0
        profile.grindedCoins = 0.0
        profile.setPrestige(profile.getPrestige() + 1)
        val data = profile.unlockedPerkMap["FastPass"]
        //FastPass Perk
        if (data != null) {
            profile.experience = LevelUtil.getLevelTotalExperience(profile.getPrestige(), 50)
        }
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
            (("&e&l自动精通! &7" + RankUtil.getPlayerColoredName(player.name)).toString() + " &7解锁了精通 &e" + RomanUtil.convert(
                profile.getPrestige()
            )).toString() + " &7,gg!"
        )
        player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 1f, 1f)
    }

    companion object {
        private val getEXPMessage: String = Main.instance.config.getString("FreeCoinAndExperience.GetEXP-Message")
        private val getCoinMessage: String = Main.instance.config.getString("FreeCoinAndExperience.GetCoin-Message")
        private val getItemMessage: String = Main.instance.config.getString("FreeCoinAndExperience.GetItem-Message")
        private val getKillsMessage: String = Main.instance.config.getString("FreeCoinAndExperience.GetKills-Message")
//        private val experience = Main.instance.config.getInt("FreeCoinAndExperience.Experience")
//        private val coin = Main.instance.config.getInt("FreeCoinAndExperience.Coin")
    }
}