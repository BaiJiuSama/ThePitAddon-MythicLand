package cn.irina.thepitaddon.runnable

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.PitItem
import cn.irina.thepitaddon.manager.PitManager
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.MessageType
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.item.ItemBuilder
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
                giveExp(player)
                giveCoin(player)
                giveKillCounts(player)
                giveAfkFragment(player)
                fixMythicItemsInHandWithCoal(player)
                if (profile.level >= 120
                    && !hasAmuletInInventory(player, "amulet_shadow")
                    && !isFullLevel(player, 100)
                ) {
                    autoPrestige(player)
                    return@forEach
                }
            }
        }
    }

    private fun fixMythicItemsInHandWithCoal(player: Player) {
        if (!isVIP(player)) return
        val item = player.inventory.itemInHand
        if (item == null || item.type == Material.AIR) return
        val maxLive = ItemUtil.getItemIntData(item, "maxLive")
        val live = ItemUtil.getItemIntData(item, "live")
        if (maxLive == live || maxLive == 0) return
        if (!PitManager.hasEnoughInternalItem(player, "chunk_of_vile_item", 1)) {
            player.sendMessage(CC.translate(getNoEnoughCoalsMessage))
            return
        } else {
            player.itemInHand = fixMythicItems(item)
            PitManager.takeInterNalItem(player, "chunk_of_vile_item", 1)
            player.sendMessage(CC.translate(getSuccessfulFixItemInHand))
            player.updateInventory()
        }
    }

    private fun fixMythicItems(item: ItemStack): ItemStack {
        val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(item)
        val live = ItemUtil.getItemIntData(item, "live")
        return ItemBuilder(pitItem.toItemStack()).changeNbt("live", live + 1).build()
    }

    private fun giveExp(player: Player) {
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        val exp = profile.experience
        var rewardExp = getRewardExp(profile.prestige)
        if (!isVIP(player)) rewardExp *= 0.6
        if (isFullLevel(player, 100)
            || (profile.level == 120
                    && hasAmuletInInventory(player, "amulet_shadow"))
        ) {
            player.sendMessage(CC.translate("&b&l经验值已满! &7您已满级, 无法继续升级!"))
            return
        } else {
            player.sendMessage(CC.translate(getEXPMessage).replace("%exp%", rewardExp.toInt().toString()))
            profile.experience = exp + rewardExp
        }
    }

    private fun meetRequirements(player: Player): Boolean {
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        val prestige = profile.prestige
        val kills = profile.kills
        return prestige >= 30 && kills >= 20000
    }

    private fun isVIP(player: Player): Boolean {
        return player.hasPermission("pit.afk")
    }

    private fun giveCoin(player: Player) {
        if (!hasAmuletInInventory(player, "amulet_gold")) return
        val coin = Random.nextInt(500000) + 500000
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        profile.coins += coin
        if (coin > 0) player.sendMessage(CC.translate(getCoinMessage).replace("%coin%", coin.toString()))
    }

    private fun isFullLevel(
        player: Player,
        prestige: Int,
    ): Boolean {
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        return profile.prestige >= prestige && profile.level >= 120
    }

    private fun giveKillCounts(player: Player) {
        val playerProfile = PlayerProfile.getRawCache(player.uniqueId) ?: return
        var kills = 0
        if (isVIP(player)) {
            kills = 250
        } else if (meetRequirements(player)) {
            kills = 100
        }
        playerProfile.kills += kills
        if (kills > 0) player.sendMessage(CC.translate(getKillsMessage).replace("%kills%", kills.toString()))
    }

    private fun giveAfkFragment(player: Player) {
        var amount = Random.nextInt(2)
        val pitItem = PitItem()
        if (!meetRequirements(player)) amount = 0
        if (isVIP(player)) {
            amount = Random.nextInt(3) + 1
        }
        if (amount != 0 && hasAmuletInInventory(player, "amulet_traveller")) amount += 1
        for (i in 0 until amount) {
            player.inventory.addItem(pitItem.afkFragment)
        }
        if (amount > 0) player.sendMessage(CC.translate(getItemMessage).replace("%count%", amount.toString()))
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

    private fun hasAmuletInInventory(player: Player, internalName: String): Boolean {
        return PitManager.hasInternalItem(player, internalName)
    }

    private fun isShadowBoots(boots: ItemStack): Boolean {
        return "shadow_boots" == ItemUtil.getInternalName(boots)
    }

    private fun equippedShadowBoots(player: Player): Boolean {
        if (player.inventory.boots == null || player.inventory.boots.type == Material.AIR) return false
        val boots = player.inventory.boots
        return isShadowBoots(boots)
    }

    private fun hasShadowBootsInInventory(player: Player): Boolean {
        return PitManager.hasInternalItem(player, "shadow_boots")
    }

    private fun isChainMailGoldArmor(chestPlate: ItemStack): Boolean {
        return "chain-mail_gold_armor" == ItemUtil.getInternalName(chestPlate)
    }

    private fun equippedChainMailGoldArmor(player: Player): Boolean {
        if (player.inventory.chestplate == null || player.inventory.chestplate.type == Material.AIR) return false
        val chestPlate = player.inventory.chestplate
        return isChainMailGoldArmor(chestPlate)
    }

    private fun hasChainMailGoldArmorInInventory(player: Player): Boolean {
        return PitManager.hasInternalItem(player, "chain-mail_gold_armor")
    }

    private fun isInterstellarHelmet(helmet: ItemStack): Boolean {
        return "interstellar_helmet" == ItemUtil.getInternalName(helmet)
    }

    private fun equippedInterstellarHelmet(player: Player): Boolean {
        if (player.inventory.helmet == null || player.inventory.helmet.type == Material.AIR) return false
        val helmet = player.inventory.helmet
        return isInterstellarHelmet(helmet)
    }

    private fun hasInterstellarHelmetInInventory(player: Player): Boolean {
        return PitManager.hasInternalItem(player, "interstellar_helmet")
    }

    private fun autoPrestige(player: Player) {
        val profile = PlayerProfile.getRawCache(player.uniqueId)
        if (hasAmuletInInventory(player, "amulet_gold")) {
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
        private val getNoEnoughCoalsMessage: String =
            Main.instance.config.getString("FreeCoinAndExperience.NoEnoughCoals-Message")
        private val getSuccessfulFixItemInHand: String =
            Main.instance.config.getString("FreeCoinAndExperience.SuccessfulFixItemInHand-Message")
//        private val experience = Main.instance.config.getInt("FreeCoinAndExperience.Experience")
//        private val coin = Main.instance.config.getInt("FreeCoinAndExperience.Coin")
    }
}