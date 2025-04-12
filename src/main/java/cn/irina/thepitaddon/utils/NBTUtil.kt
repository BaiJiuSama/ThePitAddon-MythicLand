package cn.irina.thepitaddon.utils

import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.NBTTagString
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack


object NBTUtil {
    @JvmStatic
    fun addEnchantToItem(item: ItemStack?, enchantName: String, enchantLevel: Int): ItemStack {
        val nmsItem = CraftItemStack.asNMSCopy(item)

        var tag = nmsItem.tag
        if (tag == null) {
            tag = NBTTagCompound()
        }

        var extraTag = tag.getCompound("extra")
        if (extraTag == null) {
            extraTag = NBTTagCompound()
        }

        val enchList = extraTag.getList("ench", 8)

        val enchantmentData = "$enchantName:$enchantLevel"
        val nbtTagString = NBTTagString(enchantmentData)
        enchList.add(nbtTagString)

        extraTag["ench"] = enchList

        tag["extra"] = extraTag

        nmsItem.tag = tag
        return CraftItemStack.asBukkitCopy(nmsItem)
    }

    @JvmStatic
    fun changeEnchantToItem(
        item: ItemStack?,
        enchantName: String,
        toChangeEnchantName: String,
        changeLevel: Boolean
    ): ItemStack? {
        if (item == null) {
            return null
        }

        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = if (nmsItem.tag != null) nmsItem.tag else NBTTagCompound()
        val extraTag = if (tag.getCompound("extra") != null) tag.getCompound("extra") else NBTTagCompound()
        val enchList = extraTag.getList("ench", 8)

        for (i in 0..<enchList.size()) {
            var enchantString = enchList.getString(i)

            if (enchantString.contains(enchantName)) {
                if (changeLevel) {
                    enchantString = enchantString.replace(enchantName, toChangeEnchantName)
                } else {
                    val parts = enchantString.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (parts.size == 2) {
                        enchantString = toChangeEnchantName + ":" + parts[1]
                    }
                }
                enchList.a(i, NBTTagString(enchantString))
                break
            }
        }

        extraTag["ench"] = enchList
        tag["extra"] = extraTag
        nmsItem.tag = tag
        return CraftItemStack.asBukkitCopy(nmsItem)
    }


    fun getItemTierLevel(item: ItemStack?): Int {
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val nbtTag = if (nmsItem.hasTag()) nmsItem.tag else NBTTagCompound()
        val extraTag = if (nbtTag.hasKey("extra")) {
            nbtTag.getCompound("extra")
        } else {
            NBTTagCompound()
        }

        return extraTag.getInt("tier")
    }
}
