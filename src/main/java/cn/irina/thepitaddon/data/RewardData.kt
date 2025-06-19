package cn.irina.thepitaddon.data

import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/*
 * @Author Irina
 * @Date 2025/6/15 19:01
 */

object RewardData {
    val isReceivedEnchant = ConcurrentHashMap<UUID, Boolean>()
    val enchantReward = ConcurrentHashMap<UUID, EnchantData>()

    val isReceivedItem = ConcurrentHashMap<UUID, Boolean>()
    val itemReward = ConcurrentHashMap<UUID, ItemStack>()

    val isReceivedPlate = ConcurrentHashMap<UUID, Boolean>()
    val plateReward = ConcurrentHashMap<UUID, ItemStack>()
}
