package cn.irina.thepitaddon.enchantment.type.normal

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.irina.thepitaddon.ThePitAddon.Companion.instance
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@ArmorOnly
class Scurry : AbstractEnchantment(), Listener {

    override fun getEnchantName(): String {
        return "疾走"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "scurry"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.NORMAL
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7穿戴附有此附魔的神话之甲时, 移速提升 &b" + ((enchantLevel * 5) + 5) + "%"
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player

        Bukkit.getScheduler().runTaskAsynchronously(instance, Runnable {
            val enchantLevel = ThePit.getApi().getItemEnchantLevel(player.inventory.leggings, this.nbtName)

            if (enchantLevel < 1) {
                val pp: PlayerProfile = PlayerProfile.getRawCache(player.uniqueId)
                val speed = pp.moveSpeed
                if (player.walkSpeed.equals(speed)) return@Runnable
                Bukkit.getScheduler().runTask(instance) { player.walkSpeed = speed }
                return@Runnable
            }

            val walkSpeed = 0.2f * (1f + (enchantLevel * 0.05f))

            if (player.walkSpeed == walkSpeed) return@Runnable
            Bukkit.getScheduler().runTask(instance) {
                player.walkSpeed = walkSpeed
            }
        })
    }
}
