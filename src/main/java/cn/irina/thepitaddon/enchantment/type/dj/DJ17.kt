package cn.irina.thepitaddon.enchantment.type.dj

import net.mizukilab.pit.enchantment.param.item.ArmorOnly

/*
 * @Author Irina
 * @Date 2025/8/16 17:37
 */

@ArmorOnly
class DJ17 {

//    private val playerMap: MutableMap<UUID, PositionSongPlayer> = HashMap()
//    private val music: Song = NBSDecoder
//        .parse(instance.javaClass.getClassLoader()
//            .getResourceAsStream("RushE.nbs"))
//
//    init {
//
//        object : BukkitRunnable() {
//            override fun run() {
//                for (entry in HashSet(playerMap.entries)) {
//                    val player: Player? = Bukkit.getPlayer(entry.key)
//
//                    if (player == null || !player.isOnline || player.inventory.leggings == null || getItemEnchantLevel(player.inventory.leggings) <= 0) {
//                        val player = playerMap.remove(entry.key)
//                        player?.isPlaying = false
//                    }
//                }
//            }
//        }.runTaskTimerAsynchronously(instance, 20, 20)
//
//        try {
//            iSpigot.INSTANCE.addMovementHandler(this)
//        } catch (ignore: NoClassDefFoundError) {
//        }
//    }
//
//    override fun getEnchantName(): String {
//        return "DJ #17"
//    }
//
//    override fun getMaxEnchantLevel(): Int {
//        return 1
//    }
//
//    override fun getNbtName(): String {
//        return "rush_E_dj"
//    }
//
//    override fun getRarity(): EnchantmentRarity {
//        return EnchantmentRarity.OP
//    }
//
//    override fun getCooldown(): Cooldown? {
//        return null
//    }
//
//    override fun getUsefulnessLore(enchantLevel: Int): String {
//        return ("&7此附魔只能通过 &e抽奖活动 &7获得." + "/s&7向周围的玩家播放音乐: &fRush E")
//    }
//
//    override fun handle(enchantLevel: Int, target: Player) {
//        SongUtil.songPlay(target, playerMap, music)
//    }
//
//    override fun loopTick(enchantLevel: Int): Int {
//        return 10
//    }
//
//    override fun handleUpdateLocation(
//        player: Player,
//        location: Location?,
//        location1: Location?,
//        packetPlayInFlying: PacketPlayInFlying?
//    ) {
//        val songPlayer = this.playerMap[player.uniqueId]
//        songPlayer?.targetLocation = player.player.location
//    }
//
//    override fun handleUpdateRotation(
//        player: Player?,
//        location: Location?,
//        location1: Location?,
//        packetPlayInFlying: PacketPlayInFlying?
//    ) {
//    }
}