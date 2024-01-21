package org.achymake.recovery.listeners;

import org.achymake.recovery.Recovery;
import org.achymake.recovery.files.Database;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerDeath implements Listener {
    private final Recovery plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    public PlayerDeath(Recovery plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (event.getDrops().isEmpty())return;
        if (!player.hasPermission("recovery.event.death"))return;
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        getDatabase().addItems(armorStand, event.getDrops());
        armorStand.setCustomName(event.getDeathMessage());
        armorStand.setSmall(true);
        armorStand.setCollidable(false);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skull.setItemMeta(skullMeta);
        armorStand.getEquipment().setHelmet(skull);
        if (player.getEquipment().getChestplate() != null) {
            armorStand.getEquipment().setChestplate(player.getEquipment().getChestplate());
        }
        if (player.getEquipment().getLeggings() != null) {
            armorStand.getEquipment().setLeggings(player.getEquipment().getLeggings());
        }
        if (player.getEquipment().getBoots() != null) {
            armorStand.getEquipment().setBoots(player.getEquipment().getBoots());
        }
        event.getDrops().clear();
    }
}
