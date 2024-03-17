package org.achymake.recovery.listeners;

import org.achymake.recovery.Recovery;
import org.achymake.recovery.data.Database;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public record EntityDamageByEntity(Recovery plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand armorStand) {
            if (!getDatabase().hasDeathItems(armorStand))return;
            event.setCancelled(true);
        }
    }
}
