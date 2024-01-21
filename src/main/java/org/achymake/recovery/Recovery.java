package org.achymake.recovery;

import org.achymake.recovery.commands.RecoveryCommand;
import org.achymake.recovery.files.Database;
import org.achymake.recovery.files.Message;
import org.achymake.recovery.listeners.EntityDamageByEntity;
import org.achymake.recovery.listeners.PlayerDeath;
import org.achymake.recovery.listeners.PlayerInteractAtEntity;
import org.achymake.recovery.listeners.PlayerJoin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class Recovery extends JavaPlugin {
    private static Recovery instance;
    private static Database database;
    private static Message message;
    @Override
    public void onEnable() {
        instance = this;;
        message = new Message(this);
        database = new Database(this);
        commands();
        events();
        reload();
        getMessage().sendLog(Level.INFO, "Enabled " + getDescription().getName() + getDescription().getVersion());
        getUpdate();
    }
    @Override
    public void onDisable() {
        getMessage().sendLog(Level.INFO, "Disabled " + getDescription().getName() + getDescription().getVersion());
    }
    private void commands() {
        getCommand("recovery").setExecutor(new RecoveryCommand());
    }
    private void events() {
        new EntityDamageByEntity(this);
        new PlayerDeath(this);
        new PlayerInteractAtEntity(this);
        new PlayerJoin(this);
    }
    public void getUpdate(Player player) {
        if (notifyUpdate()) {
            getLatest((latest) -> {
                if (!getDescription().getVersion().equals(latest)) {
                    getMessage().send(player,"&6" + getInstance().getName() + " Update:&f " + latest);
                    getMessage().send(player,"&6Current Version: &f" + getDescription().getVersion());
                }
            });
        }
    }
    public void getUpdate() {
        if (notifyUpdate()) {
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        getMessage().sendLog(Level.INFO, "Checking latest release");
                        if (getDescription().getVersion().equals(latest)) {
                            getMessage().sendLog(Level.INFO, "You are using the latest version");
                        } else {
                            getMessage().sendLog(Level.INFO, "New Update: " + latest);
                            getMessage().sendLog(Level.INFO, "Current Version: " + getDescription().getVersion());
                        }
                    });
                }
            });
        }
    }
    public void getLatest(Consumer<String> consumer) {
        try {
            InputStream inputStream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 114623)).openStream();
            Scanner scanner = new Scanner(inputStream);
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
                scanner.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void reload() {
        File file = new File(getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                getConfig().load(file);
                getConfig().save(file);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            getConfig().options().copyDefaults(true);
            try {
                getConfig().save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
    private boolean notifyUpdate() {
        return getConfig().getBoolean("notify-update");
    }
    public Database getDatabase() {
        return database;
    }
    public Message getMessage() {
        return message;
    }
    public static Recovery getInstance() {
        return instance;
    }
}