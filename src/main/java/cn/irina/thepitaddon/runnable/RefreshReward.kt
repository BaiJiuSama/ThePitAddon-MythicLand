package cn.irina.thepitaddon.runnable

import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.data.RewardData
import cn.irina.thepitaddon.utils.Log
import net.mizukilab.pit.util.chat.CC
import java.time.ZoneId
import java.time.ZonedDateTime

/*
 * @Author Irina
 * @Date 2025/6/16 00:14
 */

class RefreshReward : Runnable {
    override fun run() {
        val now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
        val hour = now.hour
        val minute = now.minute
        
        val targetHour = listOf(7, 12, 18)
        if (minute != 0 || !targetHour.contains(hour)) return

        refresh()
        Log.send(CC.translate("&e当前北京时间: &f$hour:00 &e, 已刷新随机奖励"))
    }
    
    fun refresh() {
        RewardData.isReceivedEnchant.clear()
        RewardData.enchantReward.clear()
        RewardData.isReceivedItem.clear()
        RewardData.itemReward.clear()
        RewardData.isReceivedPlate.clear()
        RewardData.plateReward.clear()

        Main.instance.getReceiveManagerObject().clearReceivedList("Plate")
        Main.instance.getReceiveManagerObject().clearReceivedList("Enchant")
        Main.instance.getReceiveManagerObject().clearReceivedList("Item")
    }
}