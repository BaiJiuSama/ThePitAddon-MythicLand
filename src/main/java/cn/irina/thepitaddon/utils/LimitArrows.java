package cn.irina.thepitaddon.utils;

import net.mizukilab.pit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class LimitArrows implements Listener {
    @EventHandler
    public void onPlayerShootsArrow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.hasPermission("pit.shoot")) {
                fillArrows(player);
            }
        }
    }

    private void fillArrows(Player player) {    // For combat area
        int hasCount = 0;
        int canKeepCount = 32;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.ARROW) {
                hasCount += item.getAmount();
            }
        }
        if (hasCount < canKeepCount) {
            int neededCount = 32 - hasCount;
            givePitArrows(player, neededCount);
        }
        if (hasCount > canKeepCount) {
            int redundantCount = hasCount - canKeepCount;
            removePitArrows(player, redundantCount);
        }
    }

    private ItemBuilder pitArrow(int amount) {
        return new ItemBuilder(Material.ARROW)
                .internalName("default_arrow")
                .defaultItem()
                .canDrop(false)
                .canSaveToEnderChest(false)
                .amount(amount);
    }

    private void givePitArrows(Player player, int count) {
        ItemBuilder pitArrow = pitArrow(count);
        player.getInventory().addItem(pitArrow.build());
    }

    private void removePitArrows(Player player, int count) {
        ItemBuilder pitArrow = pitArrow(count);
        player.getInventory().removeItem(pitArrow.build());
    }
}
