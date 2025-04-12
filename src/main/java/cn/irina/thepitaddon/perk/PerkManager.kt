package cn.irina.thepitaddon.perk

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.util.chat.CC
import cn.irina.thepitaddon.ThePitAddon
import lombok.Getter
import org.bukkit.Bukkit
import org.reflections.Reflections

@Getter
class PerkManager {
    fun registerPerk() {
        val reflections = Reflections("cn.irina.thepitaddon.perk.type")

        val perkClasses = reflections.getSubTypesOf(
            Any::class.java
        )

        for (clazz in perkClasses) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("$PREFIX&a天赋加载: &e" + clazz.name))
        }

        ThePit.getInstance().perkFactory.init(perkClasses)
    }

    companion object {
        private const val PREFIX = ThePitAddon.PREFIX
    }
}

