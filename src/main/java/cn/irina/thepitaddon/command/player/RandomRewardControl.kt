package cn.irina.thepitaddon.command.player

import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.param.RewardData
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import org.bukkit.entity.Player

/*
 * @Author Irina
 * @Date 2025/6/15 13:59
 */

@Command(name = "randomReward")
class RandomRewardControl {
    @Execute
    fun onCommand(@Context player: Player, @Arg str: String) {
        when (str.uppercase()) {
            "OPEN" -> {
                try {
                    Main.instance.getRandomRewardObject().open(player)
                } catch (e: Exception) {
                    player.sendMessage(e.stackTraceToString())
                }
            }

            "RESET" -> {
                RewardData.isReceivedEnchant.remove(player.uniqueId)
                RewardData.enchantReward.remove(player.uniqueId)
                RewardData.isReceivedItem.remove(player.uniqueId)
                RewardData.itemReward.remove(player.uniqueId)
                RewardData.isReceivedPlate.remove(player.uniqueId)
                RewardData.plateReward.remove(player.uniqueId)
            }
        }

    }
}