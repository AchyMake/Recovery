package org.achymake.recovery.listeners;

import org.achymake.recovery.Recovery;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final Recovery plugin;
    public PlayerJoin(Recovery plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("recovery.command.reload"))return;
        plugin.getUpdate(event.getPlayer());
    }
}