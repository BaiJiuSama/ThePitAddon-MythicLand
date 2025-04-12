package cn.irina.thepitaddon.utils

object TimeUtil {
    @JvmStatic
    fun formatTotalSeconds(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
