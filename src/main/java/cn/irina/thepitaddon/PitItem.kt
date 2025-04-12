package cn.irina.thepitaddon

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.api.PitInternalHook
import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PitItem {
    private val thePit: PitInternalHook = ThePit.getApi()

    fun funkyFeather(): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&e特殊物品")
        lore.add("&7放于物品栏时,可以保护")
        lore.add("&7背包内的神话物品不会在死亡后扣除生命.")
        lore.add("&7&o此物品会在死亡后消耗")

        return ItemBuilder(Material.FEATHER).name("&3时髦的羽毛").lore(lore).internalName("funky_feather")
            .canTrade(true).canSaveToEnderChest(true).build()
    }

    fun ChunkOfVile(): ItemStack {
        return thePit.generateItem("ChunkOfVileItem")
    }

    fun Cactus(): ItemStack {
        val lore: MutableList<String> = ArrayList()

        lore.add("&e特殊物品")
        lore.add("&7手持并右键可以从九件未附魔的")
        lore.add("&7随机 &a神&c话&e之&6甲 &7选择其一.")
        lore.add(" ")
        lore.add("&7(部分特殊颜色不可选择)")

        return ItemBuilder(Material.CACTUS).name("&a哲学仙人掌").lore(lore).internalName("cactus").canTrade(true)
            .canSaveToEnderChest(true).build()
    }

    fun RandomColorMythicLegging(): ItemStack {
        return thePit.generateItem("Leggings")
    }
}
