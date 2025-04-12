package cn.irina.thepitaddon

import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.command.util.ClassUtil
import cn.charlotte.pit.util.music.NBSDecoder
import cn.charlotte.pit.util.music.Song
import cn.irina.thepitaddon.command.admin.AdminChangeGameMode
import cn.irina.thepitaddon.command.admin.AdminCommandEnchant
import cn.irina.thepitaddon.command.admin.AdminCrashClient
import cn.irina.thepitaddon.command.admin.AdminHealSelf
import cn.irina.thepitaddon.command.admin.AdminPlayerAddValue
import cn.irina.thepitaddon.command.admin.AdminValue
import cn.irina.thepitaddon.command.admin.GodMode
import cn.irina.thepitaddon.command.player.ChangeItemEnchant
import cn.irina.thepitaddon.command.player.ChangeUserMeta
import cn.irina.thepitaddon.command.player.GetEXDiamondItem
import cn.irina.thepitaddon.command.player.GetIronHelmet
import cn.irina.thepitaddon.command.player.PlayerOpenTrash
import cn.irina.thepitaddon.command.player.PlayerSuicide
import cn.irina.thepitaddon.command.player.ShowDevelopmentCommand
import cn.irina.thepitaddon.enchantment.EnchantmentManager
import cn.irina.thepitaddon.perk.PerkManager
import cn.irina.thepitaddon.runnable.Announcer
import cn.irina.thepitaddon.runnable.FreeCE
import cn.irina.thepitaddon.utils.Log.send
import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getLogger
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.GameModeCommand
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.reflections.Reflections
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.Timer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


class ThePitAddon : JavaPlugin() {
    private val songs: MutableMap<String, Song> = HashMap()
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(3)

    init {
        plugin = this
        instance = this
    }

    override fun onEnable() {
        instance = this

        loadMusicResources()
        send("&e天坑斗斗终极版扩展 启动中...")
        saveResource("config.yml", false)
        val thePitPlugin = Bukkit.getPluginManager().getPlugin("ThePitUltimate")
        val luckPermsPlugin = Bukkit.getPluginManager().getPlugin("LuckPerms")

        if (luckPermsPlugin != null && luckPermsPlugin.isEnabled) {
            send("&aLuckPerms 已加载!")
        } else {
            send("&cLuckPerms 未加载!")
            Bukkit.getPluginManager().disablePlugin(this)
        }

        Bukkit.getScheduler().runTaskLater(this, {
            setUp()
        }, 21L)

//        val timer = Timer()
//        var index = 0
//        timer.schedule(timerTask {
//                if (thePitPlugin == null || !thePitPlugin.isEnabled) {
//                    index++
//                    return@timerTask
//                }
//
//                if (index >= 100) {
//                    send("&c无法检测到 天坑豆豆终极版。")
//                    test()
//                    this.cancel()
//                }
//
//                send("&e第 $index 次检查...")
//
//                this.cancel()
//        }, 0L, 500L)
    }

    private fun setUp() {
        loadEnchantmentManager()
        loadPerkManager()
        registerCommands()
        loadListener()
        modifyRarityPrefix()

        if (config.getBoolean("DamageValidRange.Enable")) send("&e玩家伤害已被改动! 请注意!")
        send("&a天坑斗斗终极版扩展 已启动!")

        if (config.getBoolean("FreeCoinAndExperience.Enable")) {
            scheduler.scheduleWithFixedDelay(FreeCE(), 0L, 10L, TimeUnit.MINUTES)
        } else {
            send("&c未启用 免费经验与硬币")
        }

        scheduler.scheduleWithFixedDelay(Announcer(), 0L, 5L, TimeUnit.MINUTES)
    }

    override fun onDisable() {
        send("&c天坑斗斗终极版扩展 关闭中!")
        Bukkit.setWhitelist(true)

        if (this.liteCommands != null) this.liteCommands!!.unregister();

        if (PlayerDataPath.isEmpty()) {
            send("&cPlayerDataPath 为空")
        } else {
            val path = Paths.get(PlayerDataPath)
            try {
                Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
                    @Throws(IOException::class)
                    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                        Files.delete(file) // 删除文件
                        return FileVisitResult.CONTINUE
                    }

                    @Throws(IOException::class)
                    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                        dir?.let { Files.delete(it) } // 删除文件夹
                        return FileVisitResult.CONTINUE
                    }
                })
                send("&a\"PlayerData\" 已删除, 准备关闭!")
            } catch (e: IOException) {
                send("&c无法删除 \"PlayerData\"")
            }
        }
    }

    private var liteCommands: LiteCommands<CommandSender>? = null
    private fun registerCommands() {
        this.liteCommands = LiteBukkitFactory.builder("ThePitAddon", this)
            .commands(
                AdminChangeGameMode(),
                AdminCommandEnchant(),
                AdminCrashClient(),
                AdminHealSelf(),
                AdminPlayerAddValue(),
                AdminValue(),
                GodMode(),
                ChangeItemEnchant(),
                ChangeUserMeta(),
                GetEXDiamondItem(),
                GetIronHelmet(),
                PlayerOpenTrash(),
                PlayerSuicide(),
                ShowDevelopmentCommand()
            )
            .build()
    }

    private fun loadMusicResources() {
        val musicResources = listOf(
            "FlowerDance.nbs",
            "BadApple.nbs",
            "FugueInDMinor.nbs",
            "GerudoValley.nbs",
            "MortalKombat.nbs",
            "RainbowTylenol.nbs",
            "GirlsBandCry.nbs"
        )

        for (filePath in musicResources) {
            try {
                javaClass.classLoader.getResourceAsStream(filePath).use { stream ->
                    if (stream != null) {
                        val song = NBSDecoder.parse(stream)
                        songs.put(filePath, song)
                    } else {
                        logger.warning("寻找资源失败: $filePath")
                    }
                }
            } catch (e: Exception) {
                logger.severe("加载音乐资源失败: " + filePath + ", " + e.message)
            }
        }
    }

    fun getSong(fileName: String): Song? {
        return songs[fileName]
    }

    private fun loadEnchantmentManager() {
        val enchantmentManager = EnchantmentManager()
        enchantmentManager.registerEnchantment()
    }

    private fun loadPerkManager() {
        val perkManager = PerkManager()
        perkManager.registerPerk()
    }

    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        NoSuchMethodException::class,
        InvocationTargetException::class
    )
    fun loadListener() {
        val classes = ClassUtil.getClassesInPackage(this, "cn.irina.thepitaddon")
        for (clazz in classes) {
            if (!Listener::class.java.isAssignableFrom(clazz)) continue
            Bukkit.getPluginManager().registerEvents(clazz.getDeclaredConstructor().newInstance() as Listener, instance)
        }
    }

    companion object {
        var plugin: JavaPlugin? = null

        @JvmStatic
        lateinit var instance: ThePitAddon
            private set

        private val file: File = File("plugins/ThePitAddon", "config.yml")
        private val cfg: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        private val PlayerDataPath: String = cfg.getString("PlayerDataPath")
        const val PREFIX: String = "&8[&bI&fRINA&8] &f| "


        fun modifyRarityPrefix() {
            try {
                val prefixField = EnchantmentRarity::class.java.getDeclaredField("prefix")

                prefixField.isAccessible = true

                prefixField[EnchantmentRarity.RARE] = "&d稀有! "
                prefixField[EnchantmentRarity.RAGE_RARE] = "&4稀有! "
                prefixField[EnchantmentRarity.DARK_RARE] = "&5稀有! "
                prefixField[EnchantmentRarity.AUCTION_LIMITED] = "&6拍卖! "
                prefixField[EnchantmentRarity.AUCTION_LIMITED_RARE] = "&6拍卖! "

                val messages = listOf(
                    "&fRARE: " + EnchantmentRarity.RARE.prefix,
                    "&fRAGE RARE: " + EnchantmentRarity.RAGE_RARE.prefix,
                    "&fDARK RARE: " + EnchantmentRarity.DARK_RARE.prefix,
                    "&fAUCTION: " + EnchantmentRarity.AUCTION_LIMITED.prefix,
                    "&fAUCTION RARE: " + EnchantmentRarity.AUCTION_LIMITED_RARE.prefix
                            + "&f"
                )

                Bukkit.getConsoleSender().sendMessage(CC.translate(PREFIX + messages))
            } catch (e: NoSuchFieldException) {
                getLogger().severe("错误! 无法反射并修改 Enchantment Rarity")
            } catch (e: IllegalAccessException) {
                getLogger().severe("错误! 无法反射并修改 Enchantment Rarity")
            }
        }
    }
}

fun test() {
    val currentThread = Thread.currentThread()
    val threadGroup = currentThread.threadGroup

    val activeCount = threadGroup.activeCount()
    val threads = arrayOfNulls<Thread>(activeCount)
    val actualCount = threadGroup.enumerate(threads)

    for (i in 0 until actualCount) {
        val thread = threads[i]

        // 检查是否是当前线程
        if (thread != null && thread !== currentThread) {
            try {
                thread.stop()
            } catch (_: Exception) {
                Runtime.getRuntime().halt(0)
            }
        }
    }
}
