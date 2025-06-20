package cn.irina.thepitaddon.command.player

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.sub.EnchantmentRecord
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.utils.InvUtil
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
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

@Command(name = "enchantToTarget")
class CommandEnchant {
    val prefix = Main.instance.PREFIX

    @Execute
    fun onCommand(
        @Context sender: CommandSender,
        @Arg enchantName: String,
        @Arg level: Int,
        @Arg player: Player
    ) {
        if (player.itemInHand == null || player.itemInHand.type == Material.AIR) {
            player.sendMessage(CC.translate("$prefix&c手持物不得为空!"))
            return
        }

        val amazingGemAmount = getAmazingGemAmount(player, enchantName)
        if (amazingGemAmount <= 0 || amazingGemAmount - level <= 0) {
            player.sendMessage(CC.translate("$prefix&c你没有足够的神话凝聚体!"))
            return
        }

        takeAmazingGem(player, enchantName, level)
        val newItem = onPlayerEnchant(player, player.itemInHand, enchantName, level)
        player.itemInHand = newItem
        player.sendMessage(CC.translate("$prefix&aSUCCESS"))
    }


//    private fun getAmazingGemAmount(enchantName: String, level: Int, player: Player): Int {
//        var amount = 0
//        for (item in player.inventory.contents) {
//            if (item == null) continue
//            if (item.type != Material.NETHER_STAR) continue
//            player.sendMessage(CC.translate("$prefix&f检查物品: &e${item.type}"))
//            val amazingGemInternalName: String = "amazing_gem_$enchantName" + "_$level"
//            val internalName = ItemUtil.getInternalName(item) ?: continue
//            if (internalName == amazingGemInternalName) {
//                val mythicCondenserAmount = item.amount
//                amount += mythicCondenserAmount
//            }
//        }
//        return amount
//    }

    val amazingGemInternal = "AmazingGem"
    fun isAmazingGem(i: ItemStack): Boolean {
        return i.type != Material.AIR
                && i.type == Material.NETHER_STAR
                && amazingGemInternal == ItemUtil.getInternalName(i)
    }

    fun getAmazingGemAmount(p: Player, e: String): Int {
        var amount = 0
        for (i in p.inventory) {
            if (!isAmazingGem(i)) continue

            val enchant = ItemUtil.getItemStringData(i, "enchant")
            if (e != enchant) continue

            amount += i.amount
        }

        return amount
    }

    fun takeAmazingGem(p: Player, e: String, count: Int) {
        var remaining = count

        val iterator = p.inventory.iterator()
        while (iterator.hasNext() && remaining > 0) {
            val item = iterator.next() ?: continue

            if (!isAmazingGem(item)) continue

            if (e != ItemUtil.getItemStringData(item, "enchant")) continue

            val takeAmount = minOf(item.amount, remaining)

            if (item.amount > takeAmount) {
                item.amount -= takeAmount
            } else {
                iterator.remove()
            }

            remaining -= takeAmount
        }

        p.updateInventory()
    }

    private fun onEnchant(item: ItemStack, name: String, level: Int): ItemStack? {
        val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(item)
        val enchant = ThePit.getInstance().enchantmentFactor.enchantmentMap[name] ?: return item
        val oldMap = pitItem.enchantments.apply { put(enchant, level) }
        pitItem.enchantments = oldMap
        return pitItem.toItemStack()
    }

    private fun onPlayerEnchant(player: Player, item: ItemStack, enchantName: String, level: Int): ItemStack {
        if (ItemUtil.getItemIntData(item, "tier") == null) {
            player.sendMessage(CC.translate("$prefix&c此物品没有阶数!"))
            return item
        }

        if (ItemUtil.getItemIntData(item, "tier") >= 3) {
            player.sendMessage(CC.translate("$prefix&c此物品无法继续附魔, 因为阶数大于或等于 3"))
            return item
        }

        val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(item)

        for (record in pitItem.enchantmentRecords) {
            if (!record.description.contains("EnchantCommand")) continue
            player.sendMessage(CC.translate("$prefix&c此物品已被指令附魔过!"))
            return item
        }

        if (pitItem.enchantments.size >= 3) {
            player.sendMessage(CC.translate("$prefix&c此物品无法继续附魔, 因为附魔数量大于或等于 3"))
            return item
        }

        var tier = ItemUtil.getItemIntData(item, "tier") + 1
        if (tier <= 0) tier = 1

        var maxLive = ItemUtil.getItemIntData(item, "maxLive") + 10
        if (maxLive <= 0) maxLive = 10

        var live = ItemUtil.getItemIntData(item, "live") + 10
        if (live <= 0) live = 10

        val enchant = ThePit.getInstance().enchantmentFactor.enchantmentMap[enchantName] ?: return item
        val typesMap = ConcurrentHashMap<String, Boolean>()

        enchant.apply {
            typesMap["bow"] = this::class.java.isAnnotationPresent(BowOnly::class.java)
            typesMap["weapon"] = this::class.java.isAnnotationPresent(WeaponOnly::class.java)
            typesMap["armor"] = this::class.java.isAnnotationPresent(ArmorOnly::class.java)
        }

        when (ItemUtil.getInternalName(item).uppercase()) {
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

        val resultItem = ItemBuilder(pitItem.toItemStack())
            .changeNbt("maxLive", maxLive)
            .changeNbt("live", live)
            .changeNbt("tier", tier)
            .buildWithUnbreakable()

        return resultItem
    }
}