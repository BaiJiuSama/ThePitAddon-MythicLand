package cn.irina.thepitaddon.manager

import cn.irina.thepitaddon.Main
import net.mizukilab.pit.util.chat.CC
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

/*
* @Author ShanguanLinG
* @Date 2025/9/13 23:52
*/

class KBManager : Listener {
    companion object {
        private const val REGULARITY = "regularity"
        private const val GRIM_REAPER = "grim_reaper_enchant"

        private val kbAPI = Main.instance.kbmAPI
    }

    private val pitManager = PitManager
    private fun hasEnchant(item: ItemStack, enchant: String): Boolean {
        return pitManager.hasPitEnchant(item, enchant)
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        runCatching {
            val damager = event.damager as? Player ?: return
            val other: Player = event.entity as? Player ?: return

            kbAPI?.let { api ->
                val kbFileName = api.getKBFile(other)
                other.sendMessage(CC.translate("$other kbFileName: $kbFileName"))
                val inventory = damager.inventory
                val leggings = inventory.leggings
                if (inventory == null || leggings == null) {
                    api.setKBFile(other, "default")
                    other.sendMessage(CC.translate("&csetKBFile default."))
                    return
                }
                when {
                    hasEnchant(leggings, REGULARITY) -> {
                        api.setKBFile(other, REGULARITY)
                        other.sendMessage(CC.translate("&csetKBFile $REGULARITY."))
                        return
                    }

                    hasEnchant(leggings, GRIM_REAPER) -> {
                        api.setKBFile(other, GRIM_REAPER)
                        other.sendMessage(CC.translate("&csetKBFile $GRIM_REAPER."))
                        return
                    }

                    else -> {
                        api.setKBFile(other, "default")
                        other.sendMessage(CC.translate("&csetKBFile default."))
                        return
                    }
                }
            }
        }
    }
}