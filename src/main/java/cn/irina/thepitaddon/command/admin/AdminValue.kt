package cn.irina.thepitaddon.command.admin

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.util.chat.CC
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.entity.Player

@Command(name = "value")
@Permission("pit.admin")
class AdminValue {

    @Execute(name = "setPlayerSpeed")
    fun setSpeed(sender: Player, @Arg target: Player, @Arg value: Float) {
        val targetProfile = PlayerProfile.getRawCache(target.uniqueId)
        target.walkSpeed = value
        targetProfile.moveSpeed = value
        sender.sendMessage(CC.translate("&aSUCCESS, Now " + target.name + "'s speed is " + target.walkSpeed))
        sender.sendMessage(CC.translate("&aSUCCESS, Now " + target.name + "'s Profile Speed is " + targetProfile.moveSpeed))
    }

    @Execute(name = "getPlayerSpeed")
    fun getSpeed(sender: Player, @Arg target: Player) {
        val targetProfile = PlayerProfile.getRawCache(target.uniqueId)
        sender.sendMessage(CC.translate(target.name + "'s speed is " + target.walkSpeed))
        sender.sendMessage(CC.translate(target.name + "'s Profile Speed is " + targetProfile.moveSpeed))
    }

    @Execute(name = "getEnchant")
    fun getEnchant(
        sender: Player,
        @Arg enchantNBTName: String,
        @Arg enchantLevel: Int
    ) {
        val enchant = ThePit.getInstance().enchantmentFactor.enchantmentMap[enchantNBTName]
        if (enchant != null && enchantLevel > 0) {
            sender.sendMessage(CC.translate("&b|| &f附魔: &f" + enchant.enchantName))
            sender.sendMessage(CC.translate("&b|| &f描述: &f" + enchant.getUsefulnessLore(enchantLevel)))
        } else if (enchantLevel < 0) {
            sender.sendMessage(CC.translate("&c错误的附魔等级 <EnchantLevel > 0>"))
        } else {
            sender.sendMessage(CC.translate("&c未找到 &e$enchantNBTName &c,可能并不存在这个附魔?"))
        }
    }
}
