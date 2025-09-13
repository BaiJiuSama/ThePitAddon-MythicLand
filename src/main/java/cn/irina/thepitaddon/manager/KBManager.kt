package cn.irina.thepitaddon.manager

import cn.irina.thepitaddon.Main
import net.mizukilab.pit.util.chat.CC
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

class KBManager : Listener {

    /*
    * @Author ShanguanLinG
    * @Date 2025/9/13 23:52
    */


    private val kbAPI = Main.instance.kbmAPI

    private fun hasRegEnchant(leggings: ItemStack): Boolean {
        val hasRegEnchant = leggings.let {
            PitManager.hasPitEnchant(
                it,
                "regularity"
            )
        }
        return hasRegEnchant
    }

    private fun hasGrimReaperEnchant(leggings: ItemStack): Boolean {
        return leggings?.let {
            PitManager.hasPitEnchant(
                it,
                "grim_reaper_enchant"
            )
        } ?: false
    }

    @Throws(NullPointerException::class)
    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as Player
        val other: Player = event.entity as Player
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
                hasRegEnchant(leggings) -> {
                    api.setKBFile(other, "regularity")
                    other.sendMessage(CC.translate("&csetKBFile regularity."))
                    return
                }

                hasGrimReaperEnchant(leggings) -> {
                    api.setKBFile(other, "grim_reaper_enchant")
                    other.sendMessage(CC.translate("&csetKBFile grim_reaper_enchant."))
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