package cn.irina.thepitaddon.data

import net.mizukilab.pit.enchantment.AbstractEnchantment

/*
 * @Author Irina
 * @Date 2025/6/15 01:26
 */

data class EnchantData(
    var level: Int,
    val enchant: AbstractEnchantment
)
