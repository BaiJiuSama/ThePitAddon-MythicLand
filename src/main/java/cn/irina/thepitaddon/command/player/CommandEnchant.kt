package cn.irina.thepitaddon.command.player

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.sub.EnchantmentRecord
import cn.irina.thepitaddon.Main
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.param.item.BowOnly
import net.mizukilab.pit.enchantment.param.item.WeaponOnly
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.item.ItemBuilder
import net.mizukilab.pit.util.item.ItemUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.java

@Command(name = "enchantToHand")
class CommandEnchant {
    val prefix = Main.instance.PREFIX

    @Execute
    fun onCommand(
        @Context sender: CommandSender,
        @Arg str: String,
        @Arg level: Int
    ) {
        val player = sender as? Player ?: return

        val enchantedItem = if (player.hasPermission("pit.admin")) {
            onEnchant(player.itemInHand, str, level)
        } else {
            onPlayerEnchant(player, player.itemInHand, str, level)
        }

        player.itemInHand = enchantedItem
        player.sendMessage(CC.translate("&aSUCCESS!"))
    }

    fun onEnchant(item: ItemStack, name: String, level: Int): ItemStack? {
        val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(item)
        val enchant = ThePit.getInstance().enchantmentFactor.enchantmentMap[name] ?: return null
        val oldMap = pitItem.enchantments.apply { put(enchant, level) }
        pitItem.enchantments = oldMap
        return pitItem.toItemStack()
    }

    fun onPlayerEnchant(player: Player, item: ItemStack, enchantName: String, level: Int): ItemStack? {
        if (ItemUtil.getItemIntData(item, "tier") >= 3) {
            player.sendMessage(CC.translate("&c此物品无法继续附魔, 因为阶数大于或等于 3"))
            return item
        }

        val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(item)
        if (pitItem.enchantments.size >= 3) {
            player.sendMessage(CC.translate("&c此物品无法继续附魔, 因为附魔数量大于或等于 3"))
            return item
        }

        var tier = ItemUtil.getItemIntData(item, "tier") + 1
        if (tier <= 0) tier = 1

        var maxLive = ItemUtil.getItemIntData(item, "maxLive") + 10
        if (maxLive <= 0) maxLive = 10

        var live = ItemUtil.getItemIntData(item, "live") + 10
        if (live <= 0) live = 10

        val enchant = ThePit.getInstance().enchantmentFactor.enchantmentMap[enchantName] ?: return null
        val typesMap = ConcurrentHashMap<String, Boolean>()

        enchant.apply {
            typesMap["bow"] = this::class.java.isAnnotationPresent(BowOnly::class.java)
            typesMap["weapon"] = this::class.java.isAnnotationPresent(WeaponOnly::class.java)
            typesMap["armor"] = this::class.java.isAnnotationPresent(ArmorOnly::class.java)
        }

        when (ItemUtil.getInternalName(item).uppercase())  {
            "MYTHIC_SWORD" -> {
                if (typesMap["weapon"] == false) {
                    player.sendMessage(CC.translate("$prefix&c此附魔不能附魔在 &e神话之剑 &c上!"))
                    return item
                }
            }

            "MYTHIC_BOW" -> {
                if (typesMap["bow"] == false) {
                    player.sendMessage(CC.translate("$prefix&c此附魔不能附魔在 &e神话之弓 &c上!"))
                    return item
                }
            }

            "MYTHIC_LEGGINGS" -> {
                if (typesMap["armor"] == false) {
                    player.sendMessage(CC.translate("$prefix&c此附魔不能附魔在 &e神话之甲 &c上!"))
                    return item
                }
            }

            else -> {
                player.sendMessage(CC.translate("$prefix&c请手持对应可被附魔的物品!"))
                return item
            }
        }

        val oldMap = pitItem.enchantments.apply {
            val hasEnchantLevel = ThePit.getApi().getItemEnchantLevel(item, enchant.nbtName)
            if (hasEnchantLevel >= 1) {
                put(enchant, if (hasEnchantLevel + level > 3) 3 else hasEnchantLevel + level)
            } else {
                put(enchant, if (level > 3) 3 else level)
            }
        }

        pitItem.enchantments = oldMap
        pitItem.enchantmentRecords += EnchantmentRecord(
            player.displayName,
            "EnchantCommand",
            System.currentTimeMillis()
        )

        return ItemBuilder(pitItem.toItemStack())
            .changeNbt("maxLive", maxLive)
            .changeNbt("live", live)
            .changeNbt("tier", tier)
            .buildWithUnbreakable()
    }
}