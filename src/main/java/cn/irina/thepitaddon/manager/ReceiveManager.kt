package cn.irina.thepitaddon.manager

import cn.irina.thepitaddon.Main
import cn.irina.thepitaddon.data.RewardData
import cn.irina.thepitaddon.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

/*
 * @Author Irina
 * @Date 2025/7/5 20:53
 */

object ReceiveManager: CoroutineScope {
    private val job = Job()
    override val coroutineContext = job + Dispatchers.Default

    private val plugin = Main.instance
    private val prefix = Main.instance.PREFIX

    lateinit var configFile: File
    lateinit var config: YamlConfiguration

    private var rewardData: RewardData? = null

    val enchantReceivedList = ArrayList<String>()
    val itemReceivedList = ArrayList<String>()
    val plateReceivedList = ArrayList<String>()

    init {
        loadReceiveFile()
        rewardData = Main.instance.getRandomRewardObject().rewardData
    }

    fun loadReceiveFile() {
        configFile = File(plugin.dataFolder, "ReceiveData.yml")

        if (!configFile.exists()) {
            try {
                configFile.createNewFile()
                Log.send("&a已创建 &f\"ReceivedData.yml\"")
            } catch (e: Exception) {
                Log.send(e.localizedMessage)
                return
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile)
        Log.send("&fReceivedData.yml &a已加载")
    }

    fun saveReceiveData() {
        launch {
            try {
                config.save(configFile)
                Log.send("&fReceivedData.yml &a保存完毕!")
            } catch (e: Exception) {
                Log.send(e.localizedMessage)
            }
        }
    }

    fun getReceivedPlayersUUID(path: String): Set<String> {
        return config.getStringList("Reward.$path").toHashSet()
    }

    fun loadReceivedData(path: String) {
        getReceivedPlayersUUID(path).forEach {
            when (path.uppercase()) {
                "ENCHANT" -> {
                    enchantReceivedList.add(it)
                    rewardData!!.isReceivedEnchant[UUID.fromString(it)] = true
                }
                "ITEM" -> {
                    itemReceivedList.add(it)
                    rewardData!!.isReceivedItem[UUID.fromString(it)] = true
                }
                "PLATE" -> {
                    plateReceivedList.add(it)
                    rewardData!!.isReceivedPlate[UUID.fromString(it)] = true
                }
                else -> {
                    Log.send("&c$it")
                    Log.send("&c该UUID无法被加载, 有点莫名其妙啊...")
                    return@forEach
                }
            }
        }
    }

    fun addReceivedToConfig(path: String, uuid: UUID) {
        launch {
            val list = config.getStringList("Reward.$path")
            list.add(uuid.toString())
            config.set("Reward.$path", list)

            saveReceiveData()
        }
    }

    fun clearReceivedList(path: String) {
        launch {
            config.getStringList("Reward.$path").clear()

            saveReceiveData()
        }
    }
}