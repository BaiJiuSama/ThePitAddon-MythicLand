package cn.irina.thepitaddon.utils

import cn.charlotte.pit.util.chat.CC
import cn.irina.thepitaddon.ThePitAddon
import org.bukkit.Bukkit

object Log {
    private const val PREFIX = ThePitAddon.PREFIX

    @JvmStatic
    fun send(message: String) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(PREFIX + message))
    }
}
