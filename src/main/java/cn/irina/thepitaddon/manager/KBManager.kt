package cn.irina.thepitaddon.manager

import cn.irina.thepitaddon.Main
import net.mizukilab.pit.util.chat.CC
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

class KBManager : Listener {

    private val kbAPI = Main.instance.kbmAPI

    private fun hasRegEnchant(leggings: ItemStack): Boolean? {
        val hasRegEnchant = leggings.let {
            PitManager.hasPitEnchant(
                it,
                "regularity"
            )
        }
        return hasRegEnchant
    }

    private fun hasGrimReaperEnchant(leggings: ItemStack): Boolean? {
        val hasGrimReaperEnchant = leggings.let {
            PitManager.hasPitEnchant(
                it,
                "grim_reaper_enchant"
            )
        }
        return hasGrimReaperEnchant
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val other: Player = event.entity as Player
        kbAPI?.setFilter(other, true)
        other.sendMessage(CC.translate("&csetFilter true."))
        val kbFileName = kbAPI?.getKBFile(other)
        other.sendMessage(CC.translate("$other kbFileName: $kbFileName"))
        val leggings: ItemStack = other.inventory.leggings
        when {
            hasRegEnchant(leggings) == true -> {
                kbAPI?.setKBFile(other, "regularity")
                other.sendMessage(CC.translate("&csetKBFile regularity."))
            }

            hasGrimReaperEnchant(leggings) == true -> {
                kbAPI?.setKBFile(other, "grim_reaper_enchant")
                other.sendMessage(CC.translate("&csetKBFile grim_reaper_enchant."))
            }

            else -> {
                kbAPI?.setKBFile(other, "default")
            }
        }
    }
}