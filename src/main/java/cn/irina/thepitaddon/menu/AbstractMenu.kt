package cn.irina.thepitaddon.menu

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/*
 * @Author Irina
 * @Date 2025/6/15 00:19
 */

abstract class AbstractMenu {
    protected var inventory: Inventory? = null

    abstract fun getMenuName(): String?
    abstract fun getMenuSize(): Int

    fun open(player: Player) {
        inventory = Bukkit.createInventory(null, getMenuSize(), getMenuName())
        setupItems(player)
        player.openInventory(inventory)
    }

    protected abstract fun setupItems(player: Player)

    protected fun addItemToInventory(
        slot: Int,
        its: ItemStack,
    ) {
        inventory!!.setItem(slot, its)
    }
}