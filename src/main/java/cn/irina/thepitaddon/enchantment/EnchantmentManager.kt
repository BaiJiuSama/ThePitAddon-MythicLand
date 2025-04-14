package cn.irina.thepitaddon.enchantment

import cn.charlotte.pit.ThePit
import net.mizukilab.pit.enchantment.AbstractEnchantment
import cn.irina.thepitaddon.ThePitAddon
import lombok.Getter
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity.*
import net.mizukilab.pit.util.chat.CC.*
import org.bukkit.Bukkit
import org.reflections.Reflections

@Getter
class EnchantmentManager {
    private val formatEnchantList: MutableList<String> = ArrayList()
    private val opEnchants: MutableList<String> = ArrayList()
    private val rareEnchants: MutableList<String> = ArrayList()
    private val normalEnchants: MutableList<String> = ArrayList()
    private val rageEnchants: MutableList<String> = ArrayList()
    private val rageRareEnchants: MutableList<String> = ArrayList()
    private val darkEnchants: MutableList<String> = ArrayList()
    private val darkRareEnchants: MutableList<String> = ArrayList()

    fun registerEnchantment() {
        val reflections = Reflections("cn.irina.thepitaddon.enchantment.type")

        val enchantmentClasses = reflections.getSubTypesOf(
            AbstractEnchantment::class.java
        )

        Bukkit.getConsoleSender().sendMessage(translate("$PREFIX&e扫描到的附魔类数量: ${enchantmentClasses.size}"))
        for (clazz in enchantmentClasses) {
            val newInstance = clazz.getDeclaredConstructor().newInstance()
            val enchantName = newInstance.enchantName
            when (newInstance.rarity) {
                OP -> opEnchants.add(translate("&c限定 &f$enchantName"))
                RARE -> rareEnchants.add(translate("&d稀有 &f$enchantName"))
                NORMAL -> normalEnchants.add(translate("&7普通 &f$enchantName"))
                RAGE -> rageEnchants.add(translate("&4暴怒 &f$enchantName"))
                RAGE_RARE -> rageRareEnchants.add(translate("&4暴怒稀有 &f$enchantName"))
                DARK_NORMAL -> darkEnchants.add(translate("&5暗黑 &f$enchantName"))
                DARK_RARE -> darkRareEnchants.add(translate("&5暗黑稀有 &f$enchantName"))
                else -> continue
            }
        }

        formatEnchantList.addAll(opEnchants)
        formatEnchantList.addAll(rageEnchants)
        formatEnchantList.addAll(rageRareEnchants)
        formatEnchantList.addAll(darkEnchants)
        formatEnchantList.addAll(darkRareEnchants)
        formatEnchantList.addAll(rareEnchants)
        formatEnchantList.addAll(normalEnchants)

        for (enchant in formatEnchantList) {
            Bukkit.getConsoleSender().sendMessage(translate("$PREFIX&a附魔加载: $enchant"))
        }

        ThePit.getInstance().enchantmentFactor.init(enchantmentClasses)
    }

    companion object {
        const val PREFIX = ThePitAddon.PREFIX
    }
}

