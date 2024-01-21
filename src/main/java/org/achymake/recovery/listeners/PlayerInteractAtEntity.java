package org.achymake.recovery.listeners;

import org.achymake.recovery.Recovery;
import org.achymake.recovery.files.Database;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PlayerInteractAtEntity implements Listener {
    private final Recovery plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    public PlayerInteractAtEntity(Recovery plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof ArmorStand armorStand) {
            if (!getDatabase().hasDeathItems(armorStand))return;
            event.setCancelled(true);
            getDatabase().showParticle(player, armorStand.getLocation());
            getDatabase().playSound(player);
            getDatabase().giveItems(player, armorStand);
            player.swingMainHand();
            armorStand.remove();
        }
    }
}
