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
            ) ?: false
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
        if (event.damager !is Player || event.entity !is Player) return
        val damager = event.damager as Player
        val other: Player = event.entity as Player
        if (other == null || "bot" == other.name) return
        kbAPI?.let { api ->
            val kbFile = api.getKBFile(other)
            if (kbFile == null) api.setKBFile(other, "default")
            val inventory = damager.inventory
            val leggings = inventory.leggings
            if (inventory == null || leggings == null) {
                api.setKBFile(other, "default")
                return
            }
            when {
                hasRegEnchant(leggings) -> {
                    api.setKBFile(other, "regularity")
                    return
                }

                hasGrimReaperEnchant(leggings) -> {
                    api.setKBFile(other, "grim_reaper_enchant")
                    return
                }

                else -> {
                    api.setKBFile(other, "default")
                    return
                }
            }
        }

    }
}