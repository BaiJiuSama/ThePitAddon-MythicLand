package cn.irina.thepitaddon.runnable

import net.mizukilab.pit.util.chat.CC
import cn.irina.thepitaddon.ThePitAddon


class Announcer : Runnable {
    override fun run() {
        val message = messages[currentIndex]
        CC.boardCast(message)
        currentIndex = (currentIndex + 1) % messages.size
    }

    companion object {
        private val messages: List<String> = ThePitAddon.instance.config.getStringList("Announcements")

        private var currentIndex = 0
    }
}
