package cn.irina.thepitaddon.enchantment

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.util.chat.CC
import cn.irina.thepitaddon.ThePitAddon
import lombok.Getter
import org.bukkit.Bukkit
import org.reflections.Reflections

@Getter
class EnchantmentManager {
    fun registerEnchantment() {
        val reflections = Reflections("cn.irina.thepitaddon.enchantment.type")

        val enchantmentClasses = reflections.getSubTypesOf(
            AbstractEnchantment::class.java
        )

        for (clazz in enchantmentClasses) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("$PREFIX&a附魔加载: &e" + clazz.simpleName))
        }

        ThePit.getInstance().enchantmentFactor.init(enchantmentClasses)
    }

    companion object {
        const val PREFIX = ThePitAddon.PREFIX
    }
}

