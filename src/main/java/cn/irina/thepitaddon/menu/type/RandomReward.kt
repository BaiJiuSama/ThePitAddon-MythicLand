package cn.irina.thepitaddon.menu.type

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.sub.EnchantmentRecord
import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.PitItem
import cn.irina.thepitaddon.manager.PointsManager
import cn.irina.thepitaddon.menu.AbstractMenu
import cn.irina.thepitaddon.param.EnchantData
import cn.irina.thepitaddon.param.RewardData
import net.md_5.bungee.api.ChatColor
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.param.item.ArmorOnly
import net.mizukilab.pit.enchantment.param.item.BowOnly
import net.mizukilab.pit.enchantment.param.item.WeaponOnly
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.libs.core.util.NumberUtil.range
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.RomanUtil
import net.mizukilab.pit.util.item.ItemBuilder
import net.mizukilab.pit.util.item.ItemUtil
import net.mizukilab.pit.util.random.RandomUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/*
 * @Author Irina
 * @Date 2025/6/15 00:22
 */

class RandomReward: AbstractMenu(), Listener {
    val prefix = Main.instance.PREFIX
    val pointsManager = PointsManager

    override fun getMenuName(): String {
        return CC.translate("&f&k!!&r &b流浪的魔女 &f&k!!&r")
    }

    override fun getMenuSize(): Int {
        return 27
    }

    val rewardData = RewardData
    val isReceivedEnchant = rewardData.isReceivedEnchant
    val enchantReward = rewardData.enchantReward

    val isReceivedItem = rewardData.isReceivedItem
    val itemReward = rewardData.itemReward

    val isReceivedPlate = rewardData.isReceivedPlate
    val plateReward = rewardData.plateReward
    override fun setupItems(player: Player) {
        val borderIndex = listOf(
            0, 8, 9, 17, 18, 26
        )
        for (i in borderIndex) {
            addItemToInventory(i, ItemBuilder(Material.STAINED_GLASS_PANE).shiny().name("&f&k!!&r &b随机奖励 &f&k!!&r").lore(" ").build())
        }

        if (isReceivedEnchant.getOrDefault(player.uniqueId, false)) {
            addItemToInventory(11,
                ItemBuilder(Material.BARRIER)
                    .name("&f&k!!&r &c奖励已领取! &f&k!!&r")
                    .lore(
                        "&c该奖励已领取!",
                        "&c请等待下次刷新后领取!"
                    )
                    .build()
            )
        } else {
            if (enchantReward[player.uniqueId] == null) enchantReward[player.uniqueId] = EnchantData(listOf(1, 2, 3).random(), randomEnchant())
            val enchantData = enchantReward[player.uniqueId]!!
            val enchant = enchantData.enchant
            addItemToInventory(11,
                ItemBuilder(Material.ENCHANTED_BOOK)
                    .name("&9" + enchant.enchantName + " " + RomanUtil.convert(enchantData.level))
                    .lore(
                        "",
                        CC.translate(enchant.getUsefulnessLore(enchantData.level).split("/s").toString().replace("[", "").replace("]", "")),
                        "",
                        "&7适用于:",
                        (if (enchant::class.java.isAnnotationPresent(ArmorOnly::class.java)) CC.translate("&f[ &a神话之甲 &f]") else "&f[ &c神话之甲 &f]") +
                        (if (enchant::class.java.isAnnotationPresent(WeaponOnly::class.java)) CC.translate("&f[ &a神话之剑 &f]") else "&f[ &c神话之剑 &f]") +
                        (if (enchant::class.java.isAnnotationPresent(BowOnly::class.java)) CC.translate("&f[ &a神话之弓 &f]") else "&f[ &c神话之弓 &f]"),
                        if (enchant.rarity == EnchantmentRarity.RARE)
                            if (PointsManager.getPoints(player) <= 40) "&f[ &c你没有足够的点卷 &f]" else "&f[ &a点击购买并领取 &f(40点卷) &f]"
                        else
                            "&f[ &a点击领取 &f]"
                    )
                    .changeNbt("EnchantName", enchantData.enchant.nbtName)
                    .changeNbt("EnchantLevel", enchantData.level)
                    .build()
            )
        }

        if (isReceivedItem.getOrDefault(player.uniqueId, false)) {
            addItemToInventory(13,
                ItemBuilder(Material.BARRIER)
                    .name("&f&k!!&r &c奖励已领取! &f&k!!&r")
                    .lore(
                        "&c该奖励已领取!",
                        "&c请等待下次刷新后领取!"
                    )
                    .build()
            )
        } else {
            if (itemReward[player.uniqueId] == null) itemReward[player.uniqueId] = randomItem()
            val item = itemReward[player.uniqueId]!!.clone()

            val loreList: MutableList<String> = ArrayList()

            loreList.apply {
                addAll(item.itemMeta.lore)
                addAll(listOf(
                    "",
                    "&f[ &a点击领取 &f]"
                ))
            }

            addItemToInventory(13,
                ItemBuilder(item)
                    .name(item.itemMeta.displayName)
                    .lore(loreList)
                    .amount(item.amount)
                    .build()
            )
        }

        if (isReceivedPlate.getOrDefault(player.uniqueId, false)) {
            addItemToInventory(15,
                ItemBuilder(Material.BARRIER)
                    .name("&f&k!!&r &c奖励已领取! &f&k!!&r")
                    .lore(
                        "&c该奖励已领取!",
                        "&c请等待下次刷新后领取!"
                    )
                    .build()
            )
        } else {
            if (plateReward[player.uniqueId] == null) plateReward[player.uniqueId] = randomPlate()
            val plate = plateReward[player.uniqueId]!!.clone()

            val loreList: MutableList<String> = ArrayList()

            loreList.apply {
                addAll(plate.itemMeta.lore)
                addAll(listOf(
                    "",
                    "&f[ &a点击领取 &f]"
                ))
            }

            addItemToInventory(15,
                ItemBuilder(plate)
                    .name(plate.itemMeta.displayName)
                    .lore(loreList)
                    .build()
            )
        }
    }

    fun randomEnchant(): AbstractEnchantment {
        val enchants = ThePit.getInstance().enchantmentFactor.enchantments
        val filteredEnchants = enchants
            .filter {
                if (RandomUtil.hasSuccessfullyByChance(0.3))
                    it.rarity == EnchantmentRarity.RARE
                else
                    it.rarity == EnchantmentRarity.NORMAL
            }

        return filteredEnchants.random()
    }

    val pitItem = PitItem()
    val pitItems = listOf(
        pitItem.cactus,
        pitItem.chunkOfVile,
        pitItem.funkyFeather
    )
    val rarePitItems = listOf(
        pitItem.totallyLegitGem,
        pitItem.repairKit,
        pitItem.globalAttentionGem,
        pitItem.cherry
    )
    fun randomItem(): ItemStack {
        if (RandomUtil.hasSuccessfullyByChance(0.3))
            return (rarePitItems.random()).apply {
                amount = listOf(1, 2).random()
            }

        val selectedItem = (pitItems.random()).apply {
            amount = listOf(1, 2, 3, 4, 5, 6).random()
        }

        return selectedItem
    }

    val plateItem =
        listOf(
            ItemBuilder(Material.GOLD_SWORD)
                .internalName("mythic_sword")
                .name("&e神话之剑")
                .lore(
                    "&7死亡后保留",
                    "",
                    "&7在神话之井中附魔"
                )
                .canTrade(true)
                .canSaveToEnderChest(true)
                .deathDrop(false)
                .removeOnJoin(false)
                .buildWithUnbreakable()
            ,
            ItemBuilder(Material.BOW)
                .internalName("mythic_bow")
                .name("&b神话之弓")
                .lore(
                    "&7死亡后保留",
                    "",
                    "&7在神话之井中附魔"
                )
                .canTrade(true)
                .canSaveToEnderChest(true)
                .deathDrop(false)
                .removeOnJoin(false)
                .buildWithUnbreakable()
            ,
            ThePit.getApi().generateItem("Leggings")
        )

    fun randomPlate(): ItemStack {
        val selectedItem = (plateItem.random()).apply {
            amount = listOf(1, 2, 3).random()
        }
        return selectedItem
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (!ChatColor.stripColor(event.view.title).contains("流浪的魔女")) return
        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem

        var needClaimItem: ItemStack?
        when (event.slot) {
            11 -> {
                if (rewardData.isReceivedEnchant[player.uniqueId] == true) {
                    player.sendMessage(CC.translate("$prefix&c你已领取过此奖励!"))
                    return
                }

                val itemInHand = player.itemInHand
                if (itemInHand == null || itemInHand.type == Material.AIR) {
                    player.sendMessage(CC.translate("$prefix&c你的手持物不该为空"))
                    return
                }

                val enchantData = enchantReward[player.uniqueId]
                val enchant = enchantData!!.enchant

                if (enchant.rarity == EnchantmentRarity.RARE) {
                    val pitItem = ThePit.getInstance().itemFactory.getItemFromStack(itemInHand)
                    for (e in pitItem.enchantments) {
                        if (e.key.rarity != EnchantmentRarity.RARE) continue
                        player.sendMessage(CC.translate("$prefix&c无法领取附魔, 因为此物品稀有属性大于等于 1!"))
                        return
                    }

                    if (pointsManager.getPoints(player) <= 40) {
                        player.sendMessage(CC.translate("$prefix&c你的点卷不足!"))
                        return
                    }

                    pointsManager.takePoints(player, 40)
                }

                val paramMap = HashMap<String, Boolean>()
                paramMap["bow"] = enchant::class.java.isAnnotationPresent(BowOnly::class.java)
                paramMap["weapon"] = enchant::class.java.isAnnotationPresent(WeaponOnly::class.java)
                paramMap["armor"] = enchant::class.java.isAnnotationPresent(ArmorOnly::class.java)

                when (ItemUtil.getInternalName(itemInHand).uppercase())  {
                    "MYTHIC_SWORD" -> {
                        if (paramMap["weapon"] == false) {
                            player.sendMessage(CC.translate("$prefix&c此附魔不能附魔在 &e神话之剑 &c上!"))
                            return
                        }
                    }

                    "MYTHIC_BOW" -> {
                        if (paramMap["bow"] == false) {
                            player.sendMessage(CC.translate("$prefix&c此附魔不能附魔在 &e神话之弓 &c上!"))
                            return
                        }
                    }

                    "MYTHIC_LEGGINGS" -> {
                        if (paramMap["armor"] == false) {
                            player.sendMessage(CC.translate("$prefix&c此附魔不能附魔在 &e神话之甲 &c上!"))
                            return
                        }
                    }

                    else -> {
                        player.sendMessage(CC.translate("$prefix&c请手持对应可被附魔的物品!"))
                        return
                    }
                }

                player.itemInHand = onEnchant(player, itemInHand, enchant.nbtName, enchantData.level)
                player.sendMessage(CC.translate("$prefix&a附魔领取成功!"))
                rewardData.isReceivedEnchant[player.uniqueId] = true
                rewardData.enchantReward.remove(player.uniqueId)
                player.closeInventory()
                return
            }

            13 -> {
                if (rewardData.isReceivedItem[player.uniqueId] == true) {
                    player.sendMessage(CC.translate("$prefix&c你已领取过物品奖励"))
                    return
                }

                needClaimItem = rewardData.itemReward[player.uniqueId]
                rewardData.isReceivedItem[player.uniqueId] = true
                rewardData.itemReward.remove(player.uniqueId)
            }

            15 -> {
                if (rewardData.isReceivedPlate[player.uniqueId] == true) {
                    player.sendMessage(CC.translate("$prefix&c你已领取过物品奖励"))
                    return
                }

                needClaimItem = rewardData.plateReward[player.uniqueId]
                rewardData.isReceivedPlate[player.uniqueId] = true
                rewardData.plateReward.remove(player.uniqueId)
            }

            else -> return
        }

        player.sendMessage(CC.translate("$prefix&a领取成功!"))

        val cloneItem = needClaimItem?.clone().apply { this?.amount = 1 } ?: return
        for (i in 1..item.amount)
            player.inventory.addItem(cloneItem)

        player.closeInventory()
    }

    fun onEnchant(player: Player, item: ItemStack, name: String, level: Int): ItemStack? {
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

        val enchant = ThePit.getInstance().enchantmentFactor.enchantmentMap[name] ?: return null
        val oldMap = pitItem.enchantments.apply { put(enchant, level) }

        pitItem.enchantments = oldMap
        pitItem.enchantmentRecords += EnchantmentRecord(
            player.displayName,
            "流浪的魔女",
            System.currentTimeMillis()
        )

        return ItemBuilder(pitItem.toItemStack())
            .changeNbt("maxLive", maxLive)
            .changeNbt("live", live)
            .changeNbt("tier", tier)
            .buildWithUnbreakable()
    }
}