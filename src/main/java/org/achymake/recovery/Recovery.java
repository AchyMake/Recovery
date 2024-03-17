package org.achymake.recovery;

import org.achymake.recovery.commands.RecoveryCommand;
import org.achymake.recovery.data.Database;
import org.achymake.recovery.data.Message;
import org.achymake.recovery.listeners.EntityDamageByEntity;
import org.achymake.recovery.listeners.PlayerDeath;
import org.achymake.recovery.listeners.PlayerInteractAtEntity;
import org.achymake.recovery.listeners.PlayerJoin;
import org.achymake.recovery.net.UpdateChecker;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class Recovery extends JavaPlugin {
    private static Recovery instance;
    private static Database database;
    private static Message message;
    private static UpdateChecker updateChecker;
    @Override
    public void onEnable() {
        instance = this;;
        message = new Message(this);
        database = new Database(this);
        updateChecker = new UpdateChecker(this);
        commands();
        events();
        reload();
        getMessage().sendLog(Level.INFO, "Enabled " + getDescription().getName() + getDescription().getVersion());
        getUpdateChecker().getUpdate();
    }
    @Override
    public void onDisable() {
        getMessage().sendLog(Level.INFO, "Disabled " + getDescription().getName() + getDescription().getVersion());
    }
    private void commands() {
        getCommand("recovery").setExecutor(new RecoveryCommand());
    }
    private void events() {
        getManager().registerEvents(new EntityDamageByEntity(this), this);
        getManager().registerEvents(new PlayerDeath(this), this);
        getManager().registerEvents(new PlayerInteractAtEntity(this), this);
        getManager().registerEvents(new PlayerJoin(this), this);
    }
    public void reload() {
        File file = new File(getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                getConfig().load(file);
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
    private PluginManager getManager() {
        return getServer().getPluginManager();
    }
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    public Message getMessage() {
        return message;
    }
    public Database getDatabase() {
        return database;
    }
    public static Recovery getInstance() {
        return instance;
    }
}