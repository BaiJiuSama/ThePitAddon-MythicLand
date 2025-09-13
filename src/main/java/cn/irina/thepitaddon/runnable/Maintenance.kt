package cn.irina.thepitaddon.runnable

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import net.mizukilab.pit.util.random.RandomUtil.random
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class Maintenance : Listener {
    companion object {
        @JvmField
        var isMaintenanceMode = false

        @JvmStatic
        fun isInMaintenanceMode(): Boolean = isMaintenanceMode

        @JvmStatic
        fun setMaintenanceMode(mode: Boolean) {
            isMaintenanceMode = mode
        }
    }
}