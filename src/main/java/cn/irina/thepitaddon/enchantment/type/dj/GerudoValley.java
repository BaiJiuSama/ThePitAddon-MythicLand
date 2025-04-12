package cn.irina.thepitaddon.enchantment.type.dj;

import cn.irina.thepitaddon.ThePitAddon;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.irina.thepitaddon.utils.SongUtil;

import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.music.NBSDecoder;
import cn.charlotte.pit.util.music.PositionSongPlayer;
import cn.charlotte.pit.util.music.Song;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import spg.lgdev.handler.MovementHandler;
import spg.lgdev.iSpigot;

import java.util.*;

@ArmorOnly
public class GerudoValley extends AbstractEnchantment implements  ITickTask, MovementHandler {

    private final Map<UUID, PositionSongPlayer> playerMap = new HashMap<>();
    private final Song music;

    @SneakyThrows
    public GerudoValley() {
        this.music = NBSDecoder.parse(ThePitAddon.getInstance().getClass().getClassLoader().getResourceAsStream("GerudoValley.nbs"));
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
        }.runTaskTimerAsynchronously(ThePitAddon.getInstance(), 20, 20);

        try {
            iSpigot.INSTANCE.addMovementHandler(this);
        } catch (NoClassDefFoundError ignore) {

        }
    }

    @Override
    public String getEnchantName() {
        return "DJ #9";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "gerudo_valley_dj";
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
        return "&7此附魔只能通过 &e抽奖活动 &7获得."
                + "/s&7向周围的玩家播放音乐: &fGerudo Valley";
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
}
