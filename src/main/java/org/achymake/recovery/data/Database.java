package org.achymake.recovery.data;

import org.achymake.recovery.Recovery;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;

public record Database(Recovery plugin) {
    private Message getMessage() {
        return plugin.getMessage();
    }
    public void showParticle(Player player, Location location) {
        player.spawnParticle(Particle.CLOUD, location.add(0, 0.75, 0), 75, 0.5, 0.25, 0.5, 0);
    }
    public void playSound(Player player) {
        player.playSound(player, Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
    }
    public boolean hasDeathItems(ArmorStand armorStand) {
        return armorStand.getPersistentDataContainer().has(NamespacedKey.minecraft("death-items"), PersistentDataType.STRING);
    }
    public void addItems(ArmorStand armorStand, List<ItemStack> items) {
        PersistentDataContainer data = armorStand.getPersistentDataContainer();
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeInt(items.size());
            for(int i = 0; i < items.size(); i++) {
                os.writeObject(items.get(i));
            }
            os.flush();
            byte[] rawData = io.toByteArray();
            String encodedData = Base64.getEncoder().encodeToString(rawData);
            data.set(NamespacedKey.minecraft("death-items"), PersistentDataType.STRING, encodedData);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void giveItems(Player player, Entity entity) {
        for (ItemStack itemStack : getItems(entity)) {
            if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
                player.getInventory().addItem(itemStack);
            } else {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
        }
    }
    public List<ItemStack> getItems(Entity entity) {
        List<ItemStack> drops = new ArrayList<>();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if (data.has(NamespacedKey.minecraft("death-items"))) {
            String encodedData = data.get(NamespacedKey.minecraft("death-items"), PersistentDataType.STRING);
            byte[] rawData = Base64.getDecoder().decode(encodedData);
            try {
                ByteArrayInputStream io = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream in = new BukkitObjectInputStream(io);
                int itemsCount = in.readInt();
                for(int i = 0; i < itemsCount; ++i) {
                    drops.add((ItemStack) in.readObject());
                }
                in.close();
            } catch (ClassNotFoundException | IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
        return drops;
    }
}
