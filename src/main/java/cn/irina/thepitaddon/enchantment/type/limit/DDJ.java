package cn.irina.thepitaddon.enchantment.type.limit;

import com.google.common.util.concurrent.AtomicDouble;
import net.mizukilab.pit.enchantment.AbstractEnchantment;
import net.mizukilab.pit.enchantment.param.item.WeaponOnly;
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity;
import net.mizukilab.pit.parm.listener.IAttackEntity;
import net.mizukilab.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class DDJ extends AbstractEnchantment implements IAttackEntity {

    @Override
    public String getEnchantName() {
        return "酊酮剂酮剂，大口大口嚼嚼嚼，带兴奋兴奋剂，瘾短一段带一毒胺，定通缉定通缉，druggydruggy教教教，带粟剂带粟剂，出去出去碱亢麻";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "ddj";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7攻击时将踢出目标玩家" +
                "/s" +
                "/s  \"&7&o长难句这一块\"" +
                "/s    \"&7&o家里请什么都没用了\"";

    }

    @Override
    public void handleAttackEntity(
            int i, Player player,
            Entity entity, double v,
            AtomicDouble atomicDouble,
            AtomicDouble atomicDouble1,
            AtomicBoolean atomicBoolean) {
        if (entity instanceof Player) {
            ((Player) entity).kickPlayer("Ez");
        }
    }
}
