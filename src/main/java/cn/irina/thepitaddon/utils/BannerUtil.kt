package cn.irina.thepitaddon.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.io.File

/*
 * @Author Irina
 * @Date 2025/10/4 01:08
 */

object BannerUtil {
    fun send() {

        val resource = this.javaClass.classLoader.getResource("banner.txt")
        if (resource != null) {
            val file = File(resource.path)
            file.forEachLine { Bukkit.getConsoleSender().sendMessage(color("&3$it")) }
        }
    }

    fun color(str: String): String {
        return ChatColor.translateAlternateColorCodes('&', str)
    }
}