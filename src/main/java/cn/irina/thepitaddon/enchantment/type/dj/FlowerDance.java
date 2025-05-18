package cn.irina.thepitaddon.enchantment.type.dj;

import net.mizukilab.pit.parm.listener.IAttackEntity;
import net.mizukilab.pit.parm.listener.IPlayerDamaged;
import cn.irina.thepitaddon.Main;
import net.mizukilab.pit.enchantment.AbstractEnchantment;
import cn.irina.thepitaddon.utils.SongUtil;

import net.mizukilab.pit.enchantment.param.item.ArmorOnly;
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity;
import net.mizukilab.pit.parm.listener.ITickTask;
import net.mizukilab.pit.util.cooldown.Cooldown;
import net.mizukilab.pit.util.music.NBSDecoder;
import net.mizukilab.pit.util.music.PositionSongPlayer;
import net.mizukilab.pit.util.music.Song;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import spg.lgdev.handler.MovementHandler;
import spg.lgdev.iSpigot;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class FlowerDance extends AbstractEnchantment implements  ITickTask, MovementHandler, IAttackEntity, IPlayerDamaged {

    private final Map<UUID, PositionSongPlayer> playerMap = new HashMap<>();
    private final Song music;

    @SneakyThrows
    public FlowerDance() {
        this.music = NBSDecoder.parse(Main.getInstance().getClass().getClassLoader().getResourceAsStream("FlowerDance.nbs"));
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<Map.Entry<UUID, PositionSongPlayer>> entries = new HashSet<>(playerMap.entrySet());
                for (Map.Entry<UUID, PositionSongPlayer> entry : entries) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player == null || !player.isOnline()) {
                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        remove.setPlaying(false);
                        continue;
                    }
                    if (player.getInventory().getLeggings() == null || getItemEnchantLevel(player.getInventory().getLeggings()) == -1) {
                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        remove.setPlaying(false);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20, 20);

        try {
            iSpigot.INSTANCE.addMovementHandler(this);
        } catch (NoClassDefFoundError ignore) {

        }
    }

    @Override
    public String getEnchantName() {
        return "DJ #13";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "flower_dance_dj";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7此附魔只能通过 &e抽奖活动 &7获得. /s"
                + "&7向周围的玩家播放音乐: &cFlower Dance /s"
                + "&7同时, 自身伤害 &c+8%&7, 受击伤害 &9-8%";
    }

    @Override
    public void handle(int enchantLevel, Player target) {
        SongUtil.songPlay(target, playerMap, music);
    }

    @Override
    public int loopTick(int enchantLevel) {
        return 10;
    }

    @Override
    public void handleUpdateLocation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
        PositionSongPlayer songPlayer = this.playerMap.get(player.getUniqueId());
        if (songPlayer != null) {
            songPlayer.setTargetLocation(player.getPlayer().getLocation());
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }

    @Override
    public void handleAttackEntity(int i, Player attacker, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble boostDamage, AtomicBoolean atomicBoolean) {
        boostDamage.getAndAdd(0.08);
    }

    @Override
    public void handlePlayerDamaged(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble reduceDamage, AtomicBoolean atomicBoolean) {
        reduceDamage.set(reduceDamage.get() * 0.92);
    }
}
