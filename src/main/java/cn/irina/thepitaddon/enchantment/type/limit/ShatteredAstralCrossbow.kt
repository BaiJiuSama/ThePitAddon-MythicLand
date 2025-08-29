package cn.irina.thepitaddon.enchantment.type.limit

import cn.charlotte.pit.ThePit
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.BowOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.parm.listener.IPlayerShootEntity
import net.mizukilab.pit.util.cooldown.Cooldown
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.PlayerInventory
import java.util.concurrent.atomic.AtomicBoolean

/*
 * @Author Irina
 * @Date 2025/8/20 03:52
 */

@BowOnly
class ShatteredAstralCrossbow: AbstractEnchantment(), IPlayerShootEntity, Listener {
    override fun getEnchantName(): String {
        return "殒星残弩"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "shattered_astral_crossbow"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.OP
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(i: Int): String {
        return "&7射出箭矢时, 将额外消耗 &e3 &7根箭矢 /s" +
                "&7同时, 若箭矢命中目标, 伤害将提升至 &c${100 + (i * 30)}% /s" +
                "/s" +
                "  \"&7&o诞于辰星...\" /s" +
                "    \"&7&o堕于命运...\""
    }

    override fun handleShootEntity(i: Int, player: Player, entity: Entity, p3: Double, p4: AtomicDouble?, boost: AtomicDouble, p6: AtomicBoolean?) {
        if (entity !is Player) return
        player.sendMessage("TRACK")
        boost.set(1 + (i * 0.3))
    }

    private val pitApi = ThePit.getApi()
    @EventHandler
    fun onShoot(evt: EntityShootBowEvent) {
        runCatching {
            val shooter = evt.entity as? Player ?: return
            val itemInHand = shooter.itemInHand ?: return
            if (itemInHand.type != Material.BOW || evt.projectile !is Arrow) return
            if (pitApi.getItemEnchantLevel(itemInHand, this.nbtName) < 1) return

            val arrowObj = Material.ARROW
            if (!hasEnoughItem(shooter.inventory, 3, arrowObj)) return

            removeItem(shooter.inventory, 3, arrowObj)
        }
    }

    private fun hasEnoughItem(inv: PlayerInventory, count: Int, its: Material): Boolean {
        var index = 0

        inv.forEach { item ->
            if (item == null || item.type != its) return@forEach
            index += item.amount
        }

        return index >= count
    }

    private fun removeItem(inv: PlayerInventory, count: Int, its: Material) {
        var remaining = count

        for (item in inv.contents) {
            if (remaining <= 0) break

            if (item == null || item.type != its) continue

            if (item.amount <= remaining) {
                remaining -= item.amount
                item.type = Material.AIR
            } else {
                item.amount -= remaining
                remaining = 0
            }
        }
    }
}